-- Добавляем колонку status в таблицу orders
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'NEW';
