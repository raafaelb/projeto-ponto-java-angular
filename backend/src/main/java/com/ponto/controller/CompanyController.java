package com.ponto.controller;

import com.ponto.dto.CompanyRequestDTO;
import com.ponto.dto.CompanyResponseDTO;
import com.ponto.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Companies", description = "API para gerenciamento de empresas")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor  // Injeta o Service automaticamente
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class CompanyController {
    
    private final CompanyService companyService; 
    
    @Operation(summary = "Listar todas empresas")
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> listarTodos() {
        List<CompanyResponseDTO> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }
    
    @Operation(summary = "Buscar empresa por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> buscarPorId(@PathVariable Long id) {
        CompanyResponseDTO company = companyService.findById(id);
        return ResponseEntity.ok(company);
    }
    
    @Operation(summary = "Criar nova empresa")
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> criar(@Valid @RequestBody CompanyRequestDTO companyRequestDTO) {
        CompanyResponseDTO novaCompany = companyService.create(companyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCompany);
    }
    
    @Operation(summary = "Atualizar empresa")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO) {
        CompanyResponseDTO companyAtualizada = companyService.update(id, companyRequestDTO);
        return ResponseEntity.ok(companyAtualizada);
    }
    
    @Operation(summary = "Deletar empresa")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}