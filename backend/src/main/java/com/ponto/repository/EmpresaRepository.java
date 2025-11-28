package com.ponto.repository;

import com.ponto.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByCnpj(String cnpj);
    List<Empresa> findByRazaoSocialContainingIgnoreCase(String razaoSocial);
    
    @Query("SELECT e FROM Empresa e LEFT JOIN FETCH e.funcionarios WHERE e.id = :id")
    Optional<Empresa> findByIdWithFuncionarios(Long id);
}