package com.ponto.controller;

import com.ponto.dto.OrgChartNodeDTO;
import com.ponto.service.OrgChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orgchart")
@RequiredArgsConstructor
public class OrgChartController {

    private final OrgChartService orgChartService;

    @GetMapping
    public ResponseEntity<List<OrgChartNodeDTO>> list() {
        return ResponseEntity.ok(orgChartService.listNodes());
    }
}
