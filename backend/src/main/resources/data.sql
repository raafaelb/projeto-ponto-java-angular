-- data.sql - Script de inicialização do banco

-- Garante que a tabela users tenha constraint única no username
ALTER TABLE users ADD CONSTRAINT IF NOT EXISTS uk_users_username UNIQUE (username);

-- Insere usuário admin apenas se não existir (versão compatível com PostgreSQL)
INSERT INTO users (name, email, username, password, role, data_criacao, data_atualizacao)
SELECT  'admin', 'admin@sistema.com', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeMRZD/ix3LqB.zJ6U5dMp3jIFeHvyJOC', 'ADMIN', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');