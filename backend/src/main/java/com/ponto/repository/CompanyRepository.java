package com.ponto.repository;

import com.ponto.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    boolean existsByCnpj(String cnpj);

    Optional<Company> findByCnpj(String cnpj);
    List<Company> findByRazaoSocialContainingIgnoreCase(String razaoSocial);
    
    @Query("SELECT e FROM Company e LEFT JOIN FETCH e.users WHERE e.id = :id")
    Optional<Company> findByIdWithUsers(Long id);

    @Query("SELECT c FROM Company c WHERE LOWER(c.nomeFantasia) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Company> findByNomeFantasiaContaining(@Param("nome") String nome);
}