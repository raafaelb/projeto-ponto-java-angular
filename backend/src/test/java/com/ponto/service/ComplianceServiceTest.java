package com.ponto.service;

import com.ponto.dto.PolicyDocumentRequestDTO;
import com.ponto.dto.PolicyDocumentResponseDTO;
import com.ponto.entity.Company;
import com.ponto.entity.PolicyDocument;
import com.ponto.entity.User;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.PolicyAcknowledgmentRepository;
import com.ponto.repository.PolicyDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplianceServiceTest {

    @Mock
    private PolicyDocumentRepository policyDocumentRepository;
    @Mock
    private PolicyAcknowledgmentRepository policyAcknowledgmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private ComplianceAuditService complianceAuditService;

    @InjectMocks
    private ComplianceService complianceService;

    @Test
    void createPolicyShouldReturnCreatedPolicy() {
        Company company = new Company();
        company.setId(1L);
        User manager = new User();
        manager.setRole(User.UserRole.COMPANY);
        manager.setCompany(company);

        PolicyDocumentRequestDTO request = new PolicyDocumentRequestDTO();
        request.setTitle("Codigo de Conduta");
        request.setVersion("1.0");
        request.setEffectiveDate(LocalDate.of(2026, 4, 1));
        request.setContentSummary("Regras de conduta da empresa");

        PolicyDocument saved = new PolicyDocument();
        saved.setId(9L);
        saved.setCompany(company);
        saved.setTitle(request.getTitle());
        saved.setVersion(request.getVersion());
        saved.setEffectiveDate(request.getEffectiveDate());
        saved.setContentSummary(request.getContentSummary());
        saved.setActive(true);

        when(currentUserService.getCurrentUser()).thenReturn(manager);
        doNothing().when(currentUserService).validateCompanyManagerOnly(manager);
        when(policyDocumentRepository.save(any(PolicyDocument.class))).thenReturn(saved);
        doNothing().when(complianceAuditService).log(any(), any(), any(), any(), any(), any(), any());

        PolicyDocumentResponseDTO response = complianceService.createPolicy(request);
        assertEquals(9L, response.getId());
        assertEquals("Codigo de Conduta", response.getTitle());
    }
}
