package com.ponto.service;

import com.ponto.dto.WorkdayCurrentDTO;
import com.ponto.dto.WorkdaySummaryDTO;
import com.ponto.entity.RegistroPonto;
import com.ponto.entity.User;
import com.ponto.repository.RegistroPontoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkdayServiceTest {

    @Mock
    private RegistroPontoRepository registroPontoRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private WorkdayService workdayService;

    @Test
    void currentStatusShouldReturnClockedInWhenOpenShiftExists() {
        User user = new User();
        user.setId(7L);
        user.setRole(User.UserRole.EMPLOYEE);

        RegistroPonto open = new RegistroPonto();
        open.setId(1L);
        open.setDataHoraEntrada(LocalDateTime.now().minusHours(2));

        when(currentUserService.getCurrentUser()).thenReturn(user);
        doNothing().when(currentUserService).validateEmployeeOnly(user);
        when(registroPontoRepository.findFirstByUserIdAndDataHoraSaidaIsNullOrderByDataHoraEntradaDesc(7L))
                .thenReturn(Optional.of(open));

        WorkdayCurrentDTO current = workdayService.currentStatus();

        assertTrue(current.isClockedIn());
        assertTrue(current.getWorkedMinutesUntilNow() >= 120);
    }

    @Test
    void recordsShouldAggregateWorkedMinutes() {
        User user = new User();
        user.setId(8L);
        user.setRole(User.UserRole.EMPLOYEE);

        RegistroPonto finished = new RegistroPonto();
        finished.setId(2L);
        finished.setDataHoraEntrada(LocalDateTime.of(2026, 3, 10, 9, 0));
        finished.setDataHoraSaida(LocalDateTime.of(2026, 3, 10, 17, 0));

        when(currentUserService.getCurrentUser()).thenReturn(user);
        doNothing().when(currentUserService).validateEmployeeOnly(user);
        when(registroPontoRepository.findAllByUserIdAndDataHoraEntradaBetweenOrderByDataHoraEntradaDesc(
                eq(8L), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(List.of(finished));

        WorkdaySummaryDTO summary = workdayService.records(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));

        assertEquals(1, summary.getRecords().size());
        assertEquals(480L, summary.getTotalWorkedMinutes());
    }
}
