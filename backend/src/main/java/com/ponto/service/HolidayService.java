package com.ponto.service;

import com.ponto.dto.HolidayRequestDTO;
import com.ponto.dto.HolidayResponseDTO;
import com.ponto.entity.Holiday;
import com.ponto.entity.User;
import com.ponto.exception.BusinessException;
import com.ponto.exception.ResourceNotFoundException;
import com.ponto.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<HolidayResponseDTO> list() {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        return holidayRepository.findAllByCompanyIdOrderByHolidayDateAsc(currentUser.getCompany().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public HolidayResponseDTO create(HolidayRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Long companyId = currentUser.getCompany().getId();
        String name = request.getName().trim();
        if (holidayRepository.existsByCompanyIdAndHolidayDateAndName(companyId, request.getHolidayDate(), name)) {
            throw new BusinessException("Feriado ja cadastrado com esse nome e data");
        }

        Holiday entity = new Holiday();
        entity.setCompany(currentUser.getCompany());
        entity.setHolidayDate(request.getHolidayDate());
        entity.setName(name);
        entity.setOptionalHoliday(Boolean.TRUE.equals(request.getOptionalHoliday()));
        return toResponse(holidayRepository.save(entity));
    }

    public HolidayResponseDTO update(Long id, HolidayRequestDTO request) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Holiday entity = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feriado nao encontrado"));
        currentUserService.validateCompanyAccess(currentUser, entity.getCompany().getId());

        entity.setHolidayDate(request.getHolidayDate());
        entity.setName(request.getName().trim());
        entity.setOptionalHoliday(Boolean.TRUE.equals(request.getOptionalHoliday()));
        return toResponse(holidayRepository.save(entity));
    }

    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        currentUserService.validateCompanyManagerOnly(currentUser);

        Holiday entity = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feriado nao encontrado"));
        currentUserService.validateCompanyAccess(currentUser, entity.getCompany().getId());
        holidayRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public boolean isHoliday(Long companyId, LocalDate date) {
        return holidayRepository.findAllByCompanyIdAndHolidayDateBetweenOrderByHolidayDateAsc(companyId, date, date)
                .stream()
                .anyMatch(h -> h.getHolidayDate().equals(date));
    }

    private HolidayResponseDTO toResponse(Holiday entity) {
        HolidayResponseDTO dto = new HolidayResponseDTO();
        dto.setId(entity.getId());
        dto.setHolidayDate(entity.getHolidayDate());
        dto.setName(entity.getName());
        dto.setOptionalHoliday(entity.getOptionalHoliday());
        return dto;
    }
}
