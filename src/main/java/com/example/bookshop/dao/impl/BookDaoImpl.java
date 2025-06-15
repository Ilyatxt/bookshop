package com.example.bookshop.dao.impl;

import com.example.bookshop.dao.BookDao;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Currency;
import com.example.bookshop.model.Language;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Реализация DAO для работы с книгами через JDBC
 */
@Repository
public class BookDaoImpl implements BookDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setDescription(rs.getString("description"));
        book.setLanguage(Language.valueOf(rs.getString("language_code")));
        book.setIsbn(rs.getString("isbn"));
        book.setPublishedAt(rs.getObject("published_at", LocalDate.class));
        book.setCoverImageUrl(rs.getString("cover_image_url"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setCurrency(Currency.valueOf(rs.getString("currency")));
        book.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        book.setSoldCount(rs.getInt("sold_count"));

        // Загрузка жанров для книги
        loadGenresForBook(book);

        return book;
    };

    public BookDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Загружает названия жанров для указанной книги из базы данных
     *
     * @param book книга, для которой нужно загрузить жанры
     */
    private void loadGenresForBook(Book book) {
        String sql = "SELECT g.name FROM genres g " +
                "JOIN book_genres bg ON g.id = bg.genre_id " +
                "WHERE bg.book_id = ?";

        List<String> genres = jdbcTemplate.queryForList(sql, String.class, book.getId());
        book.setGenres(genres);
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY title";
        return jdbcTemplate.query(sql, bookRowMapper);
    }

    @Override
    public List<Book> findAll(int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        String sql = "SELECT * FROM books ORDER BY title LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, bookRowMapper, pageSize, offset);
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM books";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public Optional<Book> findById(long id) {
        try {
            String sql = "SELECT * FROM books WHERE id = ?";
            Book book = jdbcTemplate.queryForObject(sql, bookRowMapper, id);
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";
        return jdbcTemplate.query(sql, bookRowMapper, "%" + title + "%");
    }

    @Override
    public List<Book> findByTitle(String title, int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) ORDER BY title LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, bookRowMapper, "%" + title + "%", pageSize, offset);
    }

    @Override
    public long countByTitle(String title) {
        String sql = "SELECT COUNT(*) FROM books WHERE LOWER(title) LIKE LOWER(?)";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, "%" + title + "%");
        return count != null ? count : 0L;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        try {
            String sql = "SELECT * FROM books WHERE isbn = ?";
            Book book = jdbcTemplate.queryForObject(sql, bookRowMapper, isbn);
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO books (title, description, language_code, isbn, published_at, "
                + "cover_image_url, price, currency, created_at, sold_count) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        book.setCreatedAt(now);

        jdbcTemplate.update(connection -> {
            // Важно передавать массив с именем поля "id"
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getDescription());
            ps.setString(3, book.getLanguage().name());
            ps.setString(4, book.getIsbn());
            ps.setObject(5, book.getPublishedAt());
            ps.setString(6, book.getCoverImageUrl());
            ps.setBigDecimal(7, book.getPrice());
            ps.setString(8, book.getCurrency().name());
            ps.setObject(9, Timestamp.from(now.toInstant()));
            ps.setInt(10, book.getSoldCount());
            return ps;
        }, keyHolder);

// Теперь keyHolder содержит только одно поле — "id"
        book.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

// Сохраняем жанры для книги, если они заданы
        saveBookGenres(book);

        return book;
    }

    @Override
    public Book update(Book book) {
        String sql = "UPDATE books SET title = ?, description = ?, language_code = ?, isbn = ?, " +
                "published_at = ?, cover_image_url = ?, price = ?, currency = ?, sold_count = ? " +
                "WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                book.getTitle(),
                book.getDescription(),
                book.getLanguage().name(),
                book.getIsbn(),
                book.getPublishedAt(),
                book.getCoverImageUrl(),
                book.getPrice(),
                book.getCurrency().name(),
                book.getSoldCount(),
                book.getId());

        if (updatedRows == 0) {
            throw new RuntimeException("Не удалось обновить книгу с ID: " + book.getId());
        }

        // Обновляем жанры для книги
        updateBookGenres(book);

        return book;
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public List<Book> findTopSellingBooks(int limit) {
        String sql = "SELECT * FROM books ORDER BY sold_count DESC LIMIT ?";
        return jdbcTemplate.query(sql, bookRowMapper, limit);
    }

    @Override
    public boolean addAuthorToBook(long bookId, long authorId) {
        String sql = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, bookId, authorId) > 0;
    }

    @Override
    public boolean removeAuthorFromBook(long bookId, long authorId) {
        String sql = "DELETE FROM book_authors WHERE book_id = ? AND author_id = ?";
        return jdbcTemplate.update(sql, bookId, authorId) > 0;
    }

    @Override
    public boolean addGenreToBook(long bookId, long genreId) {
        // Проверяем, существует ли уже такая связь
        String checkSql = "SELECT COUNT(*) FROM book_genres WHERE book_id = ? AND genre_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, bookId, genreId);

        if (count > 0) {
            return true; // Связь уже существует
        }

        String sql = "INSERT INTO book_genres (book_id, genre_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, bookId, genreId) > 0;
    }

    @Override
    public boolean removeGenreFromBook(long bookId, long genreId) {
        String sql = "DELETE FROM book_genres WHERE book_id = ? AND genre_id = ?";
        return jdbcTemplate.update(sql, bookId, genreId) > 0;
    }

    @Override
    public List<Book> findByGenre(String genreName, int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        String sql = "SELECT b.* FROM books b " +
                "JOIN book_genres bg ON b.id = bg.book_id " +
                "JOIN genres g ON bg.genre_id = g.id " +
                "WHERE LOWER(g.name) = LOWER(?) " +
                "ORDER BY b.title LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, bookRowMapper, genreName, pageSize, offset);
    }

    @Override
    public long countByGenre(String genreName) {
        String sql = "SELECT COUNT(*) FROM books b " +
                "JOIN book_genres bg ON b.id = bg.book_id " +
                "JOIN genres g ON bg.genre_id = g.id " +
                "WHERE LOWER(g.name) = LOWER(?)";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, genreName);
        return count != null ? count : 0L;
    }

    @Override
    public List<Book> findByAuthorId(long authorId) {
        String sql = "SELECT b.* FROM books b " +
                "JOIN book_authors ba ON b.id = ba.book_id " +
                "WHERE ba.author_id = ? " +
                "ORDER BY b.title";
        return jdbcTemplate.query(sql, bookRowMapper, authorId);
    }

    @Override
    public boolean existsByTitleAndAuthorId(String title, long authorId) {
        String sql = "SELECT COUNT(*) FROM books b " +
                "JOIN book_authors ba ON b.id = ba.book_id " +
                "WHERE LOWER(b.title) = LOWER(?) AND ba.author_id = ?";

        Long count = jdbcTemplate.queryForObject(sql, Long.class, title, authorId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByTitleAndAllAuthors(String title, List<Long> authorIds) {
        // Если список авторов пуст, то мы не можем проверить уникальность по авторам.
        // Возвращаем false, предполагая, что книги без авторов проверяются иначе,
        // или что это недопустимый кейс для такой проверки.
        if (authorIds == null || authorIds.isEmpty()) {
            return false;
        }

        // 1. Сортируем список authorIds, чтобы порядок не имел значения
        // (например, [1, 2] считается таким же, как [2, 1])
        List<Long> sortedAuthorIds = new ArrayList<>(authorIds);
        Collections.sort(sortedAuthorIds);

        // 2. Создаем SQL-запрос
        // Сформируем строку с плейсхолдерами для IN-условия (например, ?, ?, ?)
        String inSqlPlaceholders = String.join(",", Collections.nCopies(sortedAuthorIds.size(), "?"));

        // SQL-запрос для поиска книги с таким же названием И точным набором авторов
        // Мы ищем ID книг, которые соответствуют нашим критериям
        String sql = "SELECT b.id FROM books b " +
                "JOIN book_authors ba ON b.id = ba.book_id " +
                "WHERE LOWER(b.title) = LOWER(?) " + // Ищем по названию
                "GROUP BY b.id " + // Группируем по книге, чтобы подсчитать авторов
                // Проверяем, что количество уникальных авторов у книги совпадает с количеством переданных авторов
                "HAVING COUNT(DISTINCT ba.author_id) = ? " +
                // И что ВСЕ авторы книги находятся в нашем переданном списке authorIds
                // А также, что количество таких авторов равно размеру переданного списка (т.е. нет "лишних" авторов в книге)
                "AND COUNT(DISTINCT CASE WHEN ba.author_id IN (" + inSqlPlaceholders + ") THEN ba.author_id ELSE NULL END) = ?";

        // 3. Формируем список параметров для JdbcTemplate
        List<Object> params = new ArrayList<>();
        params.add(title);
        params.add((long) sortedAuthorIds.size()); // Параметр для COUNT(DISTINCT ba.author_id)
        params.addAll(sortedAuthorIds); // Параметры для IN-условия
        params.add((long) sortedAuthorIds.size()); // Параметр для COUNT(DISTINCT CASE WHEN ...)

        // 4. Выполняем запрос
        List<Long> existingBookIds = jdbcTemplate.queryForList(sql, Long.class, params.toArray());

        // Если список не пуст, значит, найдена книга с таким названием и таким же набором авторов
        return !existingBookIds.isEmpty();
    }
    /**
     * Сохраняет связи книги с жанрами
     *
     * @param book книга с жанрами для сохранения
     */
    private void saveBookGenres(Book book) {
        if (book.getGenres() == null || book.getGenres().isEmpty()) {
            return;
        }

        for (String genreName : book.getGenres()) {
            // Получаем id жанра по его названию
            Long genreId = getGenreIdByName(genreName);
            if (genreId == null) {
                // Если жанр не найден, можно создать новый, но по условию задачи
                // предполагаем, что все жанры уже есть в базе данных
                continue;
            }

            // Добавляем связь книги с жанром
            String sql = "INSERT INTO book_genres (book_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, book.getId(), genreId);
        }
    }

    /**
     * Обновляет связи книги с жанрами (удаляет старые и создает новые)
     *
     * @param book книга с обновленными жанрами
     */
    private void updateBookGenres(Book book) {
        // Сначала удаляем все текущие связи книги с жанрами
        String deleteSql = "DELETE FROM book_genres WHERE book_id = ?";
        jdbcTemplate.update(deleteSql, book.getId());

        // Затем добавляем новые связи
        saveBookGenres(book);
    }

    /**
     * Получает id жанра по его названию
     *
     * @param genreName название жанра
     * @return id жанра или null, если жанр не найден
     */
    private Long getGenreIdByName(String genreName) {
        try {
            String sql = "SELECT id FROM genres WHERE name = ?"; 
            return jdbcTemplate.queryForObject(sql, Long.class, genreName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> searchGenres(String query) {
        String sql = "SELECT id, name FROM genres WHERE LOWER(name) LIKE LOWER(?) ORDER BY name LIMIT 10";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", rs.getLong("id"));
            map.put("name", rs.getString("name"));
            return map;
        }, "%" + query + "%");
    }
}
