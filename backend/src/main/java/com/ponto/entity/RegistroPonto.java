package com.ponto.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_ponto")
@Data
public class RegistroPonto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;
    
    private LocalDateTime dataHoraEntrada;
    
    private LocalDateTime dataHoraSaida;
    
    private String observacao;
    
    @Column(updatable = false)
    private LocalDateTime dataCriacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
}