-- =============================================================
-- V1__initial_schema.sql  (Flyway style)
-- Initial database schema for Bookshop application
-- =============================================================
-- !! Run with UTF‑8 client encoding !!
-- =============================================================

-- 1. USERS ----------------------------------------------------
CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    email         VARCHAR(128) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'MODERATOR', 'ADMIN')),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 2. AUTHORS --------------------------------------------------
CREATE TABLE IF NOT EXISTS authors
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(64) NOT NULL,
    last_name  VARCHAR(64) NOT NULL,
    bio        TEXT,
    birthdate  DATE,
    country    VARCHAR(64)
);

-- 3. BOOKS ----------------------------------------------------
CREATE TABLE IF NOT EXISTS books
(
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    description   TEXT,
    language_code CHAR(2),
    isbn          VARCHAR(17) UNIQUE CHECK (isbn ~ '^[0-9]{10}([0-9]{3})?$'
) ,
    published_at    DATE,
    cover_image_url VARCHAR(255),
    price           NUMERIC(10,2) NOT NULL,
    currency        CHAR(3)       NOT NULL DEFAULT 'USD',
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    sold_count      INTEGER       NOT NULL DEFAULT 0 CHECK (sold_count >= 0)
);

-- 4. GENRES ---------------------------------------------------
CREATE TABLE IF NOT EXISTS genres
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

-- 5. BOOK ↔ AUTHOR (M:M) -------------------------------------
CREATE TABLE IF NOT EXISTS book_authors
(
    book_id   BIGINT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

-- 6. BOOK ↔ GENRE (M:M) --------------------------------------
CREATE TABLE IF NOT EXISTS book_genres
(
    book_id  BIGINT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

-- 7. ORDERS (header) -----------------------------------------
CREATE TABLE IF NOT EXISTS orders
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    order_date  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    order_code  VARCHAR(20)    NOT NULL UNIQUE,
    total_price NUMERIC(12, 2) NOT NULL,
    currency    CHAR(3)        NOT NULL DEFAULT 'USD'
);

CREATE INDEX IF NOT EXISTS idx_orders_user ON orders (user_id);

-- 8. ORDER_ENTRY (lines) -------------------------------------
CREATE TABLE IF NOT EXISTS order_entry
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    book_id    BIGINT         NOT NULL REFERENCES books (id),
    quantity   SMALLINT       NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_price NUMERIC(10, 2) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_order_entry_order ON order_entry (order_id);
CREATE INDEX IF NOT EXISTS idx_order_entry_book ON order_entry (book_id);

-- 9. SEED DATA (optional) ------------------------------------
-- INSERT INTO genres(name) VALUES ('Fantasy'), ('Sci‑Fi'), ('History');
-- INSERT INTO users(username,email,password_hash) VALUES ('admin','admin@example.com','stub');

-- =============================================================
-- End  V1__initial_schema.sql
-- =============================================================
