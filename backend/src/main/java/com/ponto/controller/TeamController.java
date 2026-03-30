package com.ponto.controller;

import com.ponto.dto.TeamRequestDTO;
import com.ponto.dto.TeamResponseDTO;
import com.ponto.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> listAll() {
        return ResponseEntity.ok(teamService.listAll());
    }

    @PostMapping
    public ResponseEntity<TeamResponseDTO> create(@Valid @RequestBody TeamRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> update(@PathVariable Long id, @Valid @RequestBody TeamRequestDTO request) {
        return ResponseEntity.ok(teamService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
