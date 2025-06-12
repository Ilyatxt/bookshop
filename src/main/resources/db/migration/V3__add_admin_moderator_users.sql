-- =============================================================
-- V3__add_admin_moderator_users.sql
-- Добавление пользователей с ролями ADMIN и MODERATOR
-- =============================================================

-- Добавляем пользователя с ролью ADMIN
INSERT INTO users (username, email, password_hash, role, created_at)
VALUES (
    'admin',
    'admin@bookshop.com',
    '$2a$12$rlzH/XL91ND4tT0gbtsUBe16/sfiR0MAvv41grOu3HWkYZvXcPI2y', -- пароль: 123
    'ADMIN',
    NOW()
);

-- Добавляем пользователя с ролью MODERATOR
INSERT INTO users (username, email, password_hash, role, created_at)
VALUES (
    'moderator',
    'moderator@bookshop.com',
    '$2a$12$sKnTCBFkP9XXWTrGZKiWhOFYQz6Pht7bIj9z/9wiDAmVzXHGkynfe', -- пароль: 321
    'MODERATOR',
    NOW()
);

-- =============================================================
-- End V3__add_admin_moderator_users.sql
-- =============================================================
