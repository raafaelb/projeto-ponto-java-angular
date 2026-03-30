package com.ponto.repository;

import com.ponto.entity.RegistroPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroPontoRepository extends JpaRepository<RegistroPonto, Long> {
    Optional<RegistroPonto> findFirstByUserIdAndDataHoraSaidaIsNullOrderByDataHoraEntradaDesc(Long userId);
    List<RegistroPonto> findAllByUserIdAndDataHoraEntradaBetweenOrderByDataHoraEntradaDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<RegistroPonto> findAllByUserCompanyIdAndDataHoraEntradaBetween(
            Long companyId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<RegistroPonto> findAllByUserIdAndDataHoraEntradaBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );
}
