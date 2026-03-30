package com.ponto.service;

import com.ponto.dto.*;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CareerDevelopmentService {

    private final CareerLevelRepository careerLevelRepository;
    private final SkillAssessmentRepository skillAssessmentRepository;
    private final PromotionRequestRepository promotionRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public CareerLevelResponseDTO createCareerLevel(CareerLevelRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        Long companyId = currentUser.getCompany().getId();

        if (careerLevelRepository.existsByCompanyIdAndName(companyId, request.getName().trim())) {
            throw new BusinessException("Ja existe nivel de carreira com esse nome");
        }

        CareerLevel level = new CareerLevel();
        level.setCompany(currentUser.getCompany());
        level.setName(request.getName().trim());
        level.setRankOrder(request.getRankOrder());
        level.setDescription(sanitizeOptional(request.getDescription()));
        level.setMinSalary(request.getMinSalary());
        level.setMaxSalary(request.getMaxSalary());

        return toCareerLevelResponse(careerLevelRepository.save(level));
    }

    @Transactional(readOnly = true)
    public List<CareerLevelResponseDTO> listCareerLevels() {
        User currentUser = currentUserService.getCurrentUser();
        if (!currentUserService.isCompanyManager(currentUser) && !currentUserService.isEmployee(currentUser)) {
            throw new BusinessException("Somente gestores e funcionarios podem listar niveis de carreira");
        }
        if (currentUser.getCompany() == null) {
            throw new BusinessException("Usuario sem empresa vinculada");
        }

        return careerLevelRepository.findAllByCompanyIdOrderByRankOrderAsc(currentUser.getCompany().getId())
                .stream()
                .map(this::toCareerLevelResponse)
                .toList();
    }

    public void deleteCareerLevel(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        CareerLevel level = careerLevelRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nivel de carreira nao encontrado"));
        careerLevelRepository.delete(level);
    }

    public EmployeeResponseDTO assignEmployeeLevel(Long employeeId, Long levelId) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));
        CareerLevel level = careerLevelRepository.findByIdAndCompanyId(levelId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Nivel de carreira nao encontrado"));

        employee.setCareerLevel(level);
        return toEmployeeResponse(employeeRepository.save(employee));
    }

    public SkillAssessmentResponseDTO createSkillAssessment(SkillAssessmentRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        if (request.getEmployeeId() == null) {
            throw new BusinessException("employeeId e obrigatorio");
        }
        if (request.getSkillName() == null || request.getSkillName().trim().isBlank()) {
            throw new BusinessException("skillName e obrigatorio");
        }
        if (request.getCurrentLevel() == null) {
            throw new BusinessException("currentLevel e obrigatorio");
        }

        Long companyId = currentUser.getCompany().getId();
        Employee employee = employeeRepository.findByIdAndCompanyId(request.getEmployeeId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        SkillAssessment assessment = new SkillAssessment();
        assessment.setEmployee(employee);
        assessment.setCompany(employee.getCompany());
        assessment.setSkillName(request.getSkillName().trim());
        assessment.setCurrentLevel(request.getCurrentLevel());
        assessment.setTargetLevel(request.getTargetLevel());
        assessment.setLastAssessedDate(request.getLastAssessedDate());
        assessment.setNotes(sanitizeOptional(request.getNotes()));

        return toSkillResponse(skillAssessmentRepository.save(assessment));
    }

    @Transactional(readOnly = true)
    public List<SkillAssessmentResponseDTO> listCompanySkills() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return skillAssessmentRepository.findAllByCompanyIdOrderByUpdatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toSkillResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkillAssessmentResponseDTO> listOwnSkills() {
        Employee employee = resolveCurrentEmployee();
        return skillAssessmentRepository.findAllByEmployeeIdOrderByUpdatedAtDesc(employee.getId())
                .stream()
                .map(this::toSkillResponse)
                .toList();
    }

    public PromotionRequestResponseDTO createPromotionRequest(PromotionRequestCreateDTO request) {
        Employee employee = resolveCurrentEmployee();
        Long companyId = employee.getCompany().getId();

        CareerLevel toLevel = careerLevelRepository.findByIdAndCompanyId(request.getToLevelId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Nivel de carreira destino nao encontrado"));

        PromotionRequest promotion = new PromotionRequest();
        promotion.setEmployee(employee);
        promotion.setCompany(employee.getCompany());
        promotion.setFromLevel(employee.getCareerLevel());
        promotion.setToLevel(toLevel);
        promotion.setJustification(sanitizeOptional(request.getJustification()));
        promotion.setEffectiveDate(request.getEffectiveDate());
        promotion.setStatus(PromotionStatus.PENDING);

        return toPromotionResponse(promotionRequestRepository.save(promotion));
    }

    @Transactional(readOnly = true)
    public List<PromotionRequestResponseDTO> listOwnPromotionRequests() {
        Employee employee = resolveCurrentEmployee();
        return promotionRequestRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toPromotionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PromotionRequestResponseDTO> listCompanyPromotionRequests() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return promotionRequestRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toPromotionResponse)
                .toList();
    }

    public PromotionRequestResponseDTO approvePromotionRequest(Long id, ApprovalDecisionDTO decision) {
        return reviewPromotionRequest(id, decision, PromotionStatus.APPROVED);
    }

    public PromotionRequestResponseDTO rejectPromotionRequest(Long id, ApprovalDecisionDTO decision) {
        return reviewPromotionRequest(id, decision, PromotionStatus.REJECTED);
    }

    private PromotionRequestResponseDTO reviewPromotionRequest(Long id, ApprovalDecisionDTO decision, PromotionStatus status) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        PromotionRequest request = promotionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitacao de promocao nao encontrada"));
        currentUserService.validateCompanyAccess(currentUser, request.getCompany().getId());

        if (request.getStatus() != PromotionStatus.PENDING) {
            throw new BusinessException("Solicitacao de promocao ja revisada anteriormente");
        }

        request.setStatus(status);
        request.setReviewComment(decision != null ? sanitizeOptional(decision.getComment()) : null);
        request.setReviewedBy(currentUser);
        request.setReviewedAt(LocalDateTime.now());

        if (status == PromotionStatus.APPROVED) {
            Employee employee = request.getEmployee();
            employee.setCareerLevel(request.getToLevel());
            employeeRepository.save(employee);
        }

        return toPromotionResponse(promotionRequestRepository.save(request));
    }

    private Employee resolveCurrentEmployee() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        return employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));
    }

    private CareerLevelResponseDTO toCareerLevelResponse(CareerLevel level) {
        CareerLevelResponseDTO dto = new CareerLevelResponseDTO();
        dto.setId(level.getId());
        dto.setName(level.getName());
        dto.setRankOrder(level.getRankOrder());
        dto.setDescription(level.getDescription());
        dto.setMinSalary(level.getMinSalary());
        dto.setMaxSalary(level.getMaxSalary());
        return dto;
    }

    private SkillAssessmentResponseDTO toSkillResponse(SkillAssessment assessment) {
        SkillAssessmentResponseDTO dto = new SkillAssessmentResponseDTO();
        dto.setId(assessment.getId());
        dto.setEmployeeId(assessment.getEmployee().getId());
        dto.setEmployeeName(assessment.getEmployee().getName());
        dto.setSkillName(assessment.getSkillName());
        dto.setCurrentLevel(assessment.getCurrentLevel());
        dto.setTargetLevel(assessment.getTargetLevel());
        dto.setLastAssessedDate(assessment.getLastAssessedDate());
        dto.setNotes(assessment.getNotes());
        dto.setUpdatedAt(assessment.getUpdatedAt());
        return dto;
    }

    private PromotionRequestResponseDTO toPromotionResponse(PromotionRequest request) {
        PromotionRequestResponseDTO dto = new PromotionRequestResponseDTO();
        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setEmployeeName(request.getEmployee().getName());
        dto.setFromLevelId(request.getFromLevel() != null ? request.getFromLevel().getId() : null);
        dto.setFromLevelName(request.getFromLevel() != null ? request.getFromLevel().getName() : null);
        dto.setToLevelId(request.getToLevel().getId());
        dto.setToLevelName(request.getToLevel().getName());
        dto.setJustification(request.getJustification());
        dto.setEffectiveDate(request.getEffectiveDate());
        dto.setStatus(request.getStatus());
        dto.setReviewComment(request.getReviewComment());
        dto.setReviewedBy(request.getReviewedBy() != null ? request.getReviewedBy().getName() : null);
        dto.setReviewedAt(request.getReviewedAt());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }

    private EmployeeResponseDTO toEmployeeResponse(Employee employee) {
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setPosition(employee.getPosition());
        response.setHiringDate(employee.getHiringDate());
        response.setActive(employee.getActive());
        response.setCompanyId(employee.getCompany().getId());
        response.setCompanyName(employee.getCompany().getNomeFantasia());
        response.setDepartmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null);
        response.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
        response.setTeamId(employee.getTeam() != null ? employee.getTeam().getId() : null);
        response.setTeamName(employee.getTeam() != null ? employee.getTeam().getName() : null);
        response.setManagerEmployeeId(employee.getManager() != null ? employee.getManager().getId() : null);
        response.setManagerName(employee.getManager() != null ? employee.getManager().getName() : null);
        response.setCareerLevelId(employee.getCareerLevel() != null ? employee.getCareerLevel().getId() : null);
        response.setCareerLevelName(employee.getCareerLevel() != null ? employee.getCareerLevel().getName() : null);
        response.setCurrentSalary(employee.getCurrentSalary());
        response.setUserId(employee.getUser() != null ? employee.getUser().getId() : null);
        response.setUsername(employee.getUser() != null ? employee.getUser().getUsername() : null);
        response.setDataCriacao(employee.getDataCriacao());
        response.setDataAtualizacao(employee.getDataAtualizacao());
        return response;
    }

    private String sanitizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim();
        return sanitized.isBlank() ? null : sanitized;
    }
}
