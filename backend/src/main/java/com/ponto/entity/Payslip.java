package com.ponto.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
@Data
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payroll_cycle_id", nullable = false)
    private PayrollCycle payrollCycle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "gross_pay", precision = 12, scale = 2, nullable = false)
    private BigDecimal grossPay;

    @Column(name = "deductions", precision = 12, scale = 2, nullable = false)
    private BigDecimal deductions;

    @Column(name = "tax_withheld", precision = 12, scale = 2, nullable = false)
    private BigDecimal taxWithheld;

    @Column(name = "overtime_pay", precision = 12, scale = 2, nullable = false)
    private BigDecimal overtimePay;

    @Column(name = "bonus_pay", precision = 12, scale = 2, nullable = false)
    private BigDecimal bonusPay;

    @Column(name = "net_pay", precision = 12, scale = 2, nullable = false)
    private BigDecimal netPay;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
