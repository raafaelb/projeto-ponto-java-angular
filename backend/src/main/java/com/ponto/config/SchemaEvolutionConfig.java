package com.ponto.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchemaEvolutionConfig {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    public ApplicationRunner schemaEvolutionRunner() {
        return args -> {
            // Compatibilidade com bancos antigos e migrações idempotentes.
            executeQuietly("ALTER TABLE employees ADD COLUMN IF NOT EXISTS current_salary numeric(12,2)");
            executeQuietly("ALTER TABLE employees ADD COLUMN IF NOT EXISTS career_level_id bigint");
            executeQuietly("ALTER TABLE users ADD COLUMN IF NOT EXISTS active boolean");
            executeQuietly("UPDATE users SET active = true WHERE active IS NULL");
            executeQuietly("ALTER TABLE users ALTER COLUMN active SET DEFAULT true");
            executeQuietly("ALTER TABLE users ALTER COLUMN active SET NOT NULL");

            // Estrategia segura para employee_code em tabelas ja populadas.
            executeQuietly("ALTER TABLE employees ADD COLUMN IF NOT EXISTS employee_code varchar(40)");
            executeQuietly("""
                UPDATE employees
                SET employee_code = CONCAT('LEGACY-', id)
                WHERE employee_code IS NULL OR btrim(employee_code) = ''
            """);

            // Evita falha ao criar unique constraint quando houver duplicados legados.
            executeQuietly("""
                WITH duplicates AS (
                    SELECT id,
                           ROW_NUMBER() OVER (
                               PARTITION BY company_id, employee_code
                               ORDER BY id
                           ) AS rn
                    FROM employees
                )
                UPDATE employees e
                SET employee_code = LEFT(CONCAT(COALESCE(NULLIF(btrim(e.employee_code), ''), 'LEGACY'), '-', e.id), 40)
                FROM duplicates d
                WHERE e.id = d.id
                  AND d.rn > 1
            """);

            executeQuietly("ALTER TABLE employees ALTER COLUMN employee_code SET NOT NULL");
            executeQuietly("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1
                        FROM pg_constraint
                        WHERE conname = 'uk_employees_code_company'
                    ) THEN
                        ALTER TABLE employees
                        ADD CONSTRAINT uk_employees_code_company
                        UNIQUE (employee_code, company_id);
                    END IF;
                END
                $$;
            """);

            executeQuietly("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1
                        FROM pg_constraint
                        WHERE conname = 'fk_employees_career_level'
                    ) THEN
                        ALTER TABLE employees
                        ADD CONSTRAINT fk_employees_career_level
                        FOREIGN KEY (career_level_id)
                        REFERENCES career_levels(id);
                    END IF;
                END
                $$;
            """);
        };
    }

    private void executeQuietly(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            log.warn("Schema evolution warning executing [{}]: {}", abbreviate(sql), ex.getMessage());
        }
    }

    private String abbreviate(String value) {
        String normalized = value.replaceAll("\\s+", " ").trim();
        return normalized.length() > 140 ? normalized.substring(0, 140) + "..." : normalized;
    }
}
