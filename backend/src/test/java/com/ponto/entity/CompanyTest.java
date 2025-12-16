package com.ponto.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void whenCreateCompany_thenCompanyHasCorrectProperties() {
        // Arrange & Act
        Company company = new Company();
        company.setId(1L);
        company.setCnpj("12345678000199");
        company.setRazaoSocial("Empresa Teste LTDA");
        company.setNomeFantasia("Empresa Teste");

        // Assert
        assertAll(
            () -> assertEquals(1L, company.getId()),
            () -> assertEquals("12345678000199", company.getCnpj()),
            () -> assertEquals("Empresa Teste LTDA", company.getRazaoSocial()),
            () -> assertEquals("Empresa Teste", company.getNomeFantasia())
        );
    }

    @Test
    void whenCreateCompanyWithConstructor_thenCompanyHasCorrectProperties() {
        // Arrange & Act
        Company company = new Company();
        company.setId(1L);
        company.setCnpj("12345678000199");
        company.setRazaoSocial("Empresa Teste LTDA");
        company.setNomeFantasia("Empresa Teste");

        // Assert
        assertAll(
            () -> assertEquals(1L, company.getId()),
            () -> assertEquals("12345678000199", company.getCnpj()),
            () -> assertEquals("Empresa Teste LTDA", company.getRazaoSocial()),
            () -> assertEquals("Empresa Teste", company.getNomeFantasia())
        );
    }

    @Test
    void whenCompareTwoCompanies_thenReturnCorrectEquality() {
        // Arrange
        Company company1 = new Company();
        company1.setId(1L);
        company1.setCnpj("12345678000199");
        company1.setRazaoSocial("Empresa Teste LTDA");

        Company company2 = new Company();
        company2.setId(1L);
        company2.setCnpj("12345678000199");
        company2.setRazaoSocial("Empresa Teste LTDA");

        // Assert
        assertEquals(company1, company2);
        assertEquals(company1.hashCode(), company2.hashCode());
    }
}