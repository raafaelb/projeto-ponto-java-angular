package com.ponto.service;

import com.ponto.dto.*;
import com.ponto.entity.*;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.EmployeeRepository;
import com.ponto.repository.PerformanceGoalRepository;
import com.ponto.repository.PerformanceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private final PerformanceGoalRepository goalRepository;
    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public PerformanceGoalResponseDTO createGoal(Long employeeId, PerformanceGoalRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        Employee employee = employeeRepository.findByIdAndCompanyId(employeeId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        PerformanceGoal goal = new PerformanceGoal();
        goal.setEmployee(employee);
        goal.setCompany(employee.getCompany());
        goal.setTitle(request.getTitle().trim());
        goal.setDescription(sanitizeOptional(request.getDescription()));
        goal.setWeight(request.getWeight());
        goal.setDueDate(request.getDueDate());
        goal.setStatus(request.getStatus() == null ? GoalStatus.NOT_STARTED : request.getStatus());

        return toGoalResponse(goalRepository.save(goal));
    }

    public PerformanceGoalResponseDTO updateGoal(Long id, PerformanceGoalRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        PerformanceGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta nao encontrada"));
        currentUserService.validateCompanyAccess(currentUser, goal.getCompany().getId());

        goal.setTitle(request.getTitle().trim());
        goal.setDescription(sanitizeOptional(request.getDescription()));
        goal.setWeight(request.getWeight());
        goal.setDueDate(request.getDueDate());
        goal.setStatus(request.getStatus() == null ? goal.getStatus() : request.getStatus());

        return toGoalResponse(goalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<PerformanceGoalResponseDTO> listCompanyGoals() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);
        return goalRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toGoalResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PerformanceGoalResponseDTO> listOwnGoals() {
        Employee employee = resolveCurrentEmployee();
        return goalRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toGoalResponse)
                .toList();
    }

    public PerformanceReviewResponseDTO createReview(PerformanceReviewCreateDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        if (request.getPeriodEnd().isBefore(request.getPeriodStart())) {
            throw new BusinessException("Periodo final nao pode ser anterior ao inicial");
        }

        Long companyId = currentUser.getCompany().getId();
        Employee employee = employeeRepository.findByIdAndCompanyId(request.getEmployeeId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado"));

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setCompany(employee.getCompany());
        review.setPeriodStart(request.getPeriodStart());
        review.setPeriodEnd(request.getPeriodEnd());
        review.setStatus(ReviewStatus.DRAFT);

        return toReviewResponse(reviewRepository.save(review));
    }

    public PerformanceReviewResponseDTO submitSelfReview(Long id, PerformanceReviewSelfDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);

        Employee employee = resolveCurrentEmployee();
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada"));

        if (!review.getEmployee().getId().equals(employee.getId())) {
            throw new BusinessException("Avaliacao nao pertence ao funcionario autenticado");
        }
        if (review.getStatus() == ReviewStatus.CLOSED) {
            throw new BusinessException("Avaliacao ja foi encerrada");
        }

        review.setSelfScore(request.getSelfScore());
        review.setSelfComment(sanitizeOptional(request.getSelfComment()));
        review.setStatus(ReviewStatus.SUBMITTED);

        return toReviewResponse(reviewRepository.save(review));
    }

    public PerformanceReviewResponseDTO submitManagerReview(Long id, PerformanceReviewManagerDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada"));
        currentUserService.validateCompanyAccess(currentUser, review.getCompany().getId());

        review.setManagerScore(request.getManagerScore());
        review.setManagerFeedback(sanitizeOptional(request.getManagerFeedback()));
        review.setStatus(ReviewStatus.CLOSED);
        review.setReviewedBy(currentUser);
        review.setReviewedAt(LocalDateTime.now());

        return toReviewResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponseDTO> listCompanyReviews() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        return reviewRepository.findAllByCompanyIdOrderByCreatedAtDesc(currentUser.getCompany().getId())
                .stream()
                .map(this::toReviewResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewResponseDTO> listOwnReviews() {
        Employee employee = resolveCurrentEmployee();
        return reviewRepository.findAllByEmployeeIdOrderByCreatedAtDesc(employee.getId())
                .stream()
                .map(this::toReviewResponse)
                .toList();
    }

    private Employee resolveCurrentEmployee() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateEmployeeOnly(currentUser);
        return employeeRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado para o usuario"));
    }

    private PerformanceGoalResponseDTO toGoalResponse(PerformanceGoal goal) {
        PerformanceGoalResponseDTO dto = new PerformanceGoalResponseDTO();
        dto.setId(goal.getId());
        dto.setEmployeeId(goal.getEmployee().getId());
        dto.setEmployeeName(goal.getEmployee().getName());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setWeight(goal.getWeight());
        dto.setDueDate(goal.getDueDate());
        dto.setStatus(goal.getStatus());
        dto.setCreatedAt(goal.getCreatedAt());
        return dto;
    }

    private PerformanceReviewResponseDTO toReviewResponse(PerformanceReview review) {
        PerformanceReviewResponseDTO dto = new PerformanceReviewResponseDTO();
        dto.setId(review.getId());
        dto.setEmployeeId(review.getEmployee().getId());
        dto.setEmployeeName(review.getEmployee().getName());
        dto.setPeriodStart(review.getPeriodStart());
        dto.setPeriodEnd(review.getPeriodEnd());
        dto.setSelfScore(review.getSelfScore());
        dto.setManagerScore(review.getManagerScore());
        dto.setSelfComment(review.getSelfComment());
        dto.setManagerFeedback(review.getManagerFeedback());
        dto.setStatus(review.getStatus());
        dto.setReviewedBy(review.getReviewedBy() != null ? review.getReviewedBy().getName() : null);
        dto.setReviewedAt(review.getReviewedAt());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    private String sanitizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value.trim();
        return sanitized.isBlank() ? null : sanitized;
    }
}
