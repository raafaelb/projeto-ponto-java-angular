package com.ponto.controller;

import com.ponto.dto.AttendanceAnomalyDTO;
import com.ponto.dto.AttendanceAnomalyResolveDTO;
import com.ponto.service.AttendanceAnomalyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
public class AttendanceAnomalyController {

    private final AttendanceAnomalyService anomalyService;

    @PostMapping("/generate")
    public ResponseEntity<List<AttendanceAnomalyDTO>> generate(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(anomalyService.generate(startDate, endDate));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceAnomalyDTO>> list(@RequestParam(required = false) Boolean resolved) {
        return ResponseEntity.ok(anomalyService.list(resolved));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<AttendanceAnomalyDTO> resolve(@PathVariable Long id, @RequestBody(required = false) AttendanceAnomalyResolveDTO request) {
        return ResponseEntity.ok(anomalyService.resolve(id, request));
    }
}
