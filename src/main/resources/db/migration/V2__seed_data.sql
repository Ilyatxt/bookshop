-- =============================================================
-- V2__seed_data.sql (Flyway migration)
-- Скрипт для заполнения базы данных тестовыми данными
-- =============================================================

-- 1. ЖАНРЫ ---------------------------------------------------------
INSERT INTO genres (name) VALUES
('Фантастика'),
('Детектив'),
('Роман'),
('История'),
('Научно-популярная'),
('Приключения'),
('Фэнтези');

-- 2. АВТОРЫ ---------------------------------------------------------
INSERT INTO authors (first_name, last_name, bio, birthdate, country) VALUES
('Джордж', 'Оруэлл', 'Английский писатель и публицист. Известен как автор культовых романов-антиутопий.', '1903-06-25', 'Великобритания'),
('Дж. К.', 'Роулинг', 'Британская писательница, сценаристка и кинопродюсер, наиболее известная как автор серии романов о Гарри Поттере.', '1965-07-31', 'Великобритания'),
('Айзек', 'Азимов', 'Американский писатель-фантаст, популяризатор науки, биохимик.', '1920-01-02', 'США');

-- 3. КНИГИ ---------------------------------------------------------
INSERT INTO books (title, description, language_code, isbn, published_at, cover_image_url, price, currency, sold_count) VALUES
('1984', 'Роман-антиутопия, изображающий мир тоталитарного будущего.', 'EN', '9785170827695', '1949-06-08', 'https://example.com/covers/1984.jpg', 620.00, 'RUB', 178),
('Гарри Поттер и философский камень', 'Первый роман в серии книг про юного волшебника Гарри Поттера.', 'EN', '9781408855652', '1997-06-26', 'https://example.com/covers/harry-potter-1.jpg', 790.00, 'RUB', 342),
('Основание', 'Научно-фантастический роман Айзека Азимова. Первый в серии книг "Основание".', 'EN', '9785171212834', '1951-01-01', 'https://example.com/covers/foundation.jpg', 580.00, 'RUB', 126);

-- 4. СВЯЗИ КНИГ С АВТОРАМИ -----------------------------------------
INSERT INTO book_authors (book_id, author_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 5. СВЯЗИ КНИГ С ЖАНРАМИ ------------------------------------------
INSERT INTO book_genres (book_id, genre_id) VALUES
(1, 1),
(2, 7),
(2, 6),
(3, 1),
(3, 5);

-- =============================================================
-- End V2__seed_data.sql
-- =============================================================

-- =============================================================
-- End V2__seed_data.sql
-- =============================================================
