package com.ponto.service;

import com.ponto.dto.AttendanceAnomalyDTO;
import com.ponto.dto.AttendanceAnomalyResolveDTO;
import com.ponto.entity.*;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.AbsenceRequestRepository;
import com.ponto.repository.AttendanceAnomalyRepository;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.RegistroPontoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceAnomalyService {

    private final AttendanceAnomalyRepository anomalyRepository;
    private final EmployeeRepository employeeRepository;
    private final RegistroPontoRepository registroPontoRepository;
    private final AbsenceRequestRepository absenceRequestRepository;
    private final HolidayService holidayService;
    private final CurrentUserService currentUserService;

    public List<AttendanceAnomalyDTO> generate(LocalDate startDate, LocalDate endDate) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        Long companyId = currentUser.getCompany().getId();
        List<Employee> employees = employeeRepository.findAllByCompanyIdOrderByNameAsc(companyId)
                .stream()
                .filter(Employee::getActive)
                .toList();

        for (Employee employee : employees) {
            if (employee.getUser() == null) {
                continue;
            }
            for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
                final LocalDate currentDay = day;

                if (currentDay.getDayOfWeek().getValue() >= 6) {
                    continue;
                }

                if (holidayService.isHoliday(companyId, currentDay)) {
                    continue;
                }

                boolean approvedAbsence = absenceRequestRepository
                        .findAllByCompanyIdAndStartDateBetweenOrderByStartDateAsc(companyId, currentDay, currentDay)
                        .stream()
                        .anyMatch(a -> a.getEmployee().getId().equals(employee.getId())
                                && a.getStatus() == ApprovalStatus.APPROVED
                                && !currentDay.isBefore(a.getStartDate()) && !currentDay.isAfter(a.getEndDate()));

                if (approvedAbsence) {
                    continue;
                }

                List<RegistroPonto> dayRecords = registroPontoRepository.findAllByUserIdAndDataHoraEntradaBetween(
                        employee.getUser().getId(),
                        currentDay.atStartOfDay(),
                        currentDay.atTime(LocalTime.MAX)
                );

                if (dayRecords.isEmpty()) {
                    createAnomalyIfMissing(employee, currentDay, AnomalyType.ABSENCE, "Ausencia sem registro de ponto");
                    continue;
                }

                RegistroPonto first = dayRecords.stream().min(java.util.Comparator.comparing(RegistroPonto::getDataHoraEntrada)).orElse(null);
                if (first != null && first.getDataHoraEntrada().toLocalTime().isAfter(LocalTime.of(9, 15))) {
                    createAnomalyIfMissing(employee, currentDay, AnomalyType.LATE, "Entrada apos 09:15");
                }

                boolean anyOpen = dayRecords.stream().anyMatch(r -> r.getDataHoraSaida() == null);
                if (anyOpen && !currentDay.equals(LocalDate.now())) {
                    createAnomalyIfMissing(employee, currentDay, AnomalyType.MISSING_CLOCK_OUT, "Registro sem saida");
                }
            }
        }

        return list(false);
    }

    @Transactional(readOnly = true)
    public List<AttendanceAnomalyDTO> list(Boolean resolved) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        List<AttendanceAnomaly> anomalies = resolved == null
                ? anomalyRepository.findAllByCompanyIdOrderByOccurrenceDateDescCreatedAtDesc(companyId)
                : anomalyRepository.findAllByCompanyIdAndResolvedOrderByOccurrenceDateDescCreatedAtDesc(companyId, resolved);

        return anomalies.stream().map(this::toDto).toList();
    }

    public AttendanceAnomalyDTO resolve(Long id, AttendanceAnomalyResolveDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        AttendanceAnomaly anomaly = anomalyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anomalia nao encontrada"));
        currentUserService.validateCompanyAccess(currentUser, anomaly.getCompany().getId());

        anomaly.setResolved(true);
        anomaly.setResolvedBy(currentUser);
        anomaly.setResolvedAt(LocalDateTime.now());
        if (request != null && request.getComment() != null && !request.getComment().isBlank()) {
            anomaly.setDescription((anomaly.getDescription() != null ? anomaly.getDescription() + " | " : "") +
                    "Resolucao: " + request.getComment().trim());
        }

        return toDto(anomalyRepository.save(anomaly));
    }

    @Transactional(readOnly = true)
    public long anomaliesInRange(Long companyId, Long employeeId, LocalDate start, LocalDate end) {
        return anomalyRepository.findAllByCompanyIdAndOccurrenceDateBetweenOrderByOccurrenceDateAsc(companyId, start, end)
                .stream()
                .filter(a -> a.getEmployee().getId().equals(employeeId))
                .count();
    }

    private void createAnomalyIfMissing(Employee employee, LocalDate day, AnomalyType type, String description) {
        if (anomalyRepository.existsByEmployeeIdAndOccurrenceDateAndType(employee.getId(), day, type)) {
            return;
        }

        AttendanceAnomaly anomaly = new AttendanceAnomaly();
        anomaly.setEmployee(employee);
        anomaly.setCompany(employee.getCompany());
        anomaly.setOccurrenceDate(day);
        anomaly.setType(type);
        anomaly.setDescription(description);
        anomaly.setResolved(false);
        anomalyRepository.save(anomaly);
    }

    private AttendanceAnomalyDTO toDto(AttendanceAnomaly entity) {
        AttendanceAnomalyDTO dto = new AttendanceAnomalyDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName(entity.getEmployee().getName());
        dto.setOccurrenceDate(entity.getOccurrenceDate());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setResolved(entity.getResolved());
        dto.setResolvedBy(entity.getResolvedBy() != null ? entity.getResolvedBy().getName() : null);
        dto.setResolvedAt(entity.getResolvedAt());
        return dto;
    }
}
