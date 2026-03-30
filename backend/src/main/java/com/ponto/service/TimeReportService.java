package com.ponto.service;

import com.ponto.dto.TimeReportDTO;
import com.ponto.dto.TimeReportRowDTO;
import com.ponto.entity.Employee;
import com.ponto.entity.RegistroPonto;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.RegistroPontoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeReportService {

    private final EmployeeRepository employeeRepository;
    private final RegistroPontoRepository registroPontoRepository;
    private final OvertimeService overtimeService;
    private final AbsenceRequestService absenceRequestService;
    private final AttendanceAnomalyService anomalyService;
    private final CurrentUserService currentUserService;

    public TimeReportDTO report(LocalDate startDate, LocalDate endDate) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        if (end.isBefore(start)) {
            throw new BusinessException("Data final nao pode ser anterior a data inicial");
        }

        Long companyId = currentUser.getCompany().getId();
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.atTime(LocalTime.MAX);

        List<Employee> employees = employeeRepository.findAllByCompanyIdOrderByNameAsc(companyId);
        List<TimeReportRowDTO> rows = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.getUser() == null) {
                continue;
            }
            List<RegistroPonto> registros = registroPontoRepository.findAllByUserIdAndDataHoraEntradaBetween(
                    employee.getUser().getId(), from, to
            );

            long workedMinutes = registros.stream().mapToLong(r -> {
                if (r.getDataHoraSaida() == null) {
                    return 0L;
                }
                return Duration.between(r.getDataHoraEntrada(), r.getDataHoraSaida()).toMinutes();
            }).sum();

            long approvedOvertimeMinutes = overtimeService.approvedMinutesInRange(companyId, employee.getId(), start, end);
            long absenceDays = absenceRequestService.calculateAbsenceDaysInRange(companyId, employee.getId(), start, end);
            long anomalyCount = anomalyService.anomaliesInRange(companyId, employee.getId(), start, end);

            TimeReportRowDTO row = new TimeReportRowDTO();
            row.setEmployeeId(employee.getId());
            row.setEmployeeName(employee.getName());
            row.setDepartment(employee.getDepartment() != null ? employee.getDepartment().getName() : "-");
            row.setTeam(employee.getTeam() != null ? employee.getTeam().getName() : "-");
            row.setWorkedMinutes(workedMinutes);
            row.setApprovedOvertimeMinutes(approvedOvertimeMinutes);
            row.setAbsenceDays(absenceDays);
            row.setAnomalyCount(anomalyCount);
            rows.add(row);
        }

        TimeReportDTO report = new TimeReportDTO();
        report.setRows(rows);
        report.setTotalWorkedMinutes(rows.stream().mapToLong(TimeReportRowDTO::getWorkedMinutes).sum());
        report.setTotalApprovedOvertimeMinutes(rows.stream().mapToLong(TimeReportRowDTO::getApprovedOvertimeMinutes).sum());
        report.setTotalAbsenceDays(rows.stream().mapToLong(TimeReportRowDTO::getAbsenceDays).sum());
        report.setTotalAnomalies(rows.stream().mapToLong(TimeReportRowDTO::getAnomalyCount).sum());

        return report;
    }

    public byte[] exportCsv(LocalDate startDate, LocalDate endDate) {
        TimeReportDTO report = report(startDate, endDate);
        StringBuilder sb = new StringBuilder();
        sb.append("employee_id,employee_name,department,team,worked_minutes,approved_overtime_minutes,absence_days,anomaly_count\n");

        for (TimeReportRowDTO row : report.getRows()) {
            sb.append(row.getEmployeeId()).append(',')
                    .append(escapeCsv(row.getEmployeeName())).append(',')
                    .append(escapeCsv(row.getDepartment())).append(',')
                    .append(escapeCsv(row.getTeam())).append(',')
                    .append(row.getWorkedMinutes()).append(',')
                    .append(row.getApprovedOvertimeMinutes()).append(',')
                    .append(row.getAbsenceDays()).append(',')
                    .append(row.getAnomalyCount()).append('\n');
        }

        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public byte[] exportPdf(LocalDate startDate, LocalDate endDate) {
        TimeReportDTO report = report(startDate, endDate);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
            content.beginText();
            content.newLineAtOffset(40, 770);
            content.showText("Relatorio de Ponto e Ausencias");
            content.endText();

            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
            float y = 740;
            for (TimeReportRowDTO row : report.getRows()) {
                if (y < 80) {
                    content.close();
                    page = new PDPage();
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                    y = 760;
                }

                String line = String.format(
                        "%s | %s | %s | mins:%d | extra:%d | faltas:%d | anom:%d",
                        row.getEmployeeName(),
                        row.getDepartment(),
                        row.getTeam(),
                        row.getWorkedMinutes(),
                        row.getApprovedOvertimeMinutes(),
                        row.getAbsenceDays(),
                        row.getAnomalyCount()
                );

                content.beginText();
                content.newLineAtOffset(40, y);
                content.showText(line.length() > 120 ? line.substring(0, 120) : line);
                content.endText();
                y -= 14;
            }

            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("Falha ao gerar PDF: " + e.getMessage());
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
