package com.ponto.controller;

import com.ponto.dto.HolidayRequestDTO;
import com.ponto.dto.HolidayResponseDTO;
import com.ponto.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public ResponseEntity<List<HolidayResponseDTO>> list() {
        return ResponseEntity.ok(holidayService.list());
    }

    @PostMapping
    public ResponseEntity<HolidayResponseDTO> create(@Valid @RequestBody HolidayRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(holidayService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HolidayResponseDTO> update(@PathVariable Long id, @Valid @RequestBody HolidayRequestDTO request) {
        return ResponseEntity.ok(holidayService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        holidayService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
