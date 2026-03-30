package com.ponto.controller;

import com.ponto.dto.WorkdayCurrentDTO;
import com.ponto.dto.WorkdayRecordDTO;
import com.ponto.dto.WorkdaySummaryDTO;
import com.ponto.service.WorkdayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/workday")
@RequiredArgsConstructor
public class WorkdayController {

    private final WorkdayService workdayService;

    @PostMapping("/clock-in")
    public ResponseEntity<WorkdayRecordDTO> clockIn() {
        return ResponseEntity.ok(workdayService.clockIn());
    }

    @PostMapping("/clock-out")
    public ResponseEntity<WorkdayRecordDTO> clockOut() {
        return ResponseEntity.ok(workdayService.clockOut());
    }

    @GetMapping("/current")
    public ResponseEntity<WorkdayCurrentDTO> current() {
        return ResponseEntity.ok(workdayService.currentStatus());
    }

    @GetMapping("/records")
    public ResponseEntity<WorkdaySummaryDTO> records(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(workdayService.records(startDate, endDate));
    }
}
