-- Insere o usuário admin padrão
INSERT INTO users (name, password, email, role, created_at)
VALUES ('admin', '$2a$10$5WRKtuw5CW7XkWQ6KKQjAOlmMubOaUEq0.GZIN7qKZl0psF/lmnZC', 'admin@admin.com', 'ADMIN', CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Senha: admin123

