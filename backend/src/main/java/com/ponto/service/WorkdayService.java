package com.ponto.service;

import com.ponto.dto.WorkdayCurrentDTO;
import com.ponto.dto.WorkdayRecordDTO;
import com.ponto.dto.WorkdaySummaryDTO;
import com.ponto.entity.RegistroPonto;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
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
public class WorkdayService {

    private final RegistroPontoRepository registroPontoRepository;
    private final CurrentUserService currentUserService;

    public WorkdayRecordDTO clockIn() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        registroPontoRepository.findFirstByUserIdAndDataHoraSaidaIsNullOrderByDataHoraEntradaDesc(currentUser.getId())
                .ifPresent(open -> {
                    throw new BusinessException("Ja existe jornada aberta. Finalize antes de iniciar outra.");
                });

        RegistroPonto registro = new RegistroPonto();
        registro.setUser(currentUser);
        registro.setDataHoraEntrada(LocalDateTime.now());

        return toRecord(registroPontoRepository.save(registro));
    }

    public WorkdayRecordDTO clockOut() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        RegistroPonto open = registroPontoRepository
                .findFirstByUserIdAndDataHoraSaidaIsNullOrderByDataHoraEntradaDesc(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Nenhuma jornada aberta para finalizar."));

        open.setDataHoraSaida(LocalDateTime.now());
        return toRecord(registroPontoRepository.save(open));
    }

    @Transactional(readOnly = true)
    public WorkdayCurrentDTO currentStatus() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        WorkdayCurrentDTO dto = new WorkdayCurrentDTO();

        registroPontoRepository.findFirstByUserIdAndDataHoraSaidaIsNullOrderByDataHoraEntradaDesc(currentUser.getId())
                .ifPresentOrElse(open -> {
                    dto.setClockedIn(true);
                    dto.setClockInAt(open.getDataHoraEntrada());
                    dto.setWorkedMinutesUntilNow(Duration.between(open.getDataHoraEntrada(), LocalDateTime.now()).toMinutes());
                }, () -> {
                    dto.setClockedIn(false);
                    dto.setClockInAt(null);
                    dto.setWorkedMinutesUntilNow(0L);
                });

        return dto;
    }

    @Transactional(readOnly = true)
    public WorkdaySummaryDTO records(LocalDate startDate, LocalDate endDate) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        LocalDate resolvedStart = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate resolvedEnd = endDate != null ? endDate : LocalDate.now();

        LocalDateTime from = resolvedStart.atStartOfDay();
        LocalDateTime to = resolvedEnd.atTime(LocalTime.MAX);

        List<WorkdayRecordDTO> records = registroPontoRepository
                .findAllByUserIdAndDataHoraEntradaBetweenOrderByDataHoraEntradaDesc(currentUser.getId(), from, to)
                .stream()
                .map(this::toRecord)
                .toList();

        long totalMinutes = records.stream().mapToLong(r -> r.getWorkedMinutes() == null ? 0 : r.getWorkedMinutes()).sum();

        WorkdaySummaryDTO summary = new WorkdaySummaryDTO();
        summary.setRecords(records);
        summary.setTotalWorkedMinutes(totalMinutes);
        return summary;
    }

    private WorkdayRecordDTO toRecord(RegistroPonto registro) {
        WorkdayRecordDTO dto = new WorkdayRecordDTO();
        dto.setId(registro.getId());
        dto.setClockIn(registro.getDataHoraEntrada());
        dto.setClockOut(registro.getDataHoraSaida());
        dto.setObservacao(registro.getObservacao());

        if (registro.getDataHoraSaida() == null) {
            dto.setWorkedMinutes(Duration.between(registro.getDataHoraEntrada(), LocalDateTime.now()).toMinutes());
        } else {
            dto.setWorkedMinutes(Duration.between(registro.getDataHoraEntrada(), registro.getDataHoraSaida()).toMinutes());
        }

        return dto;
    }
}
