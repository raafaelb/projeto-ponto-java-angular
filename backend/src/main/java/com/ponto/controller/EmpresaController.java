package com.ponto.controller;

import com.ponto.entity.Empresa;
import com.ponto.repository.EmpresaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpresaController {
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    @GetMapping
    public List<Empresa> listarTodos() {
        return empresaRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        Optional<Empresa> empresa = empresaRepository.findById(id);
        return empresa.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Empresa criar(@Valid @RequestBody Empresa empresa) {
        return empresaRepository.save(empresa);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, 
                                           @Valid @RequestBody Empresa empresaDetails) {
        Optional<Empresa> empresaOptional = empresaRepository.findById(id);
        
        if (empresaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Empresa empresa = empresaOptional.get();
        empresa.setRazaoSocial(empresaDetails.getRazaoSocial());
        empresa.setNomeFantasia(empresaDetails.getNomeFantasia());
        
        Empresa empresaAtualizada = empresaRepository.save(empresa);
        return ResponseEntity.ok(empresaAtualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (empresaRepository.existsById(id)) {
            empresaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}