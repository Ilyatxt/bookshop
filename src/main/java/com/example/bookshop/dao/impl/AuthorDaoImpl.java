package com.example.bookshop.dao.impl;

import com.example.bookshop.dao.AuthorDao;
import com.example.bookshop.model.Author;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Реализация DAO для работы с авторами через JDBC
 */
@Repository
public class AuthorDaoImpl implements AuthorDao {

    private static final Logger log = LoggerFactory.getLogger(AuthorDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Author> authorRowMapper = (rs, rowNum) -> {
        Author author = new Author();
        author.setId(rs.getLong("id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));
        author.setBio(rs.getString("bio"));

        Date birthdate = rs.getDate("birthdate");
        if (birthdate != null) {
            author.setBirthdate(birthdate.toLocalDate());
        }

        author.setCountry(rs.getString("country"));
        return author;
    };

    public AuthorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Author> findAll() {
        log.debug("Получение всех авторов");
        String sql = "SELECT * FROM authors ORDER BY last_name, first_name";
        List<Author> authors = jdbcTemplate.query(sql, authorRowMapper);
        log.debug("Найдено {} авторов", authors.size());
        return authors;
    }

    @Override
    public List<Author> findAll(int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        log.debug("Получение авторов: страница={}, размер={}", pageNumber, pageSize);
        String sql = "SELECT * FROM authors ORDER BY last_name, first_name LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, authorRowMapper, pageSize, offset);
    }

    @Override
    public long countAll() {
        log.debug("Подсчет общего количества авторов");
        String sql = "SELECT COUNT(*) FROM authors";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public Optional<Author> findById(long id) {
        log.debug("Поиск автора по id {}", id);
        String sql = "SELECT * FROM authors WHERE id = ?";
        List<Author> authors = jdbcTemplate.query(sql, authorRowMapper, id);
        return authors.isEmpty() ? Optional.empty() : Optional.of(authors.get(0));
    }

    @Override
    public List<Author> findByName(String name) {
        String searchPattern = "%" + name.toLowerCase() + "%";
        log.debug("Поиск авторов по имени {}", name);
        String sql = "SELECT * FROM authors WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? ORDER BY last_name, first_name";
        return jdbcTemplate.query(sql, authorRowMapper, searchPattern, searchPattern);
    }

    @Override
    public List<Author> findByName(String name, int pageNumber, int pageSize) {

        String searchPattern = "%" + name.toLowerCase() + "%";
        log.debug("Поиск авторов по имени {} страница={}, размер={}", name, pageNumber, pageSize);
        String sql = "SELECT * FROM authors WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? " +
                "ORDER BY last_name, first_name LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, authorRowMapper,
                searchPattern, searchPattern,
                pageSize, pageNumber * pageSize);
    }

    @Override
    public long countByName(String name) {
        String searchPattern = "%" + name.toLowerCase() + "%";
        log.debug("Подсчет авторов по имени {}", name);
        String sql = "SELECT COUNT(*) FROM authors WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, searchPattern, searchPattern);
        return count != null ? count : 0L;
    }

    @Override
    public List<Author> findByBookId(long bookId) {
        log.debug("Поиск авторов книги {}", bookId);
        String sql = "SELECT a.* FROM authors a JOIN book_authors ba ON a.id = ba.author_id WHERE ba.book_id = ? ORDER BY a.last_name, a.first_name";
        return jdbcTemplate.query(sql, authorRowMapper, bookId);
    }

    @Override
    public Author save(Author author) {
        log.debug("Сохранение автора {} {}", author.getFirstName(), author.getLastName());
        String sql = "INSERT INTO authors (first_name, last_name, bio, birthdate, country) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.setString(3, author.getBio());

            if (author.getBirthdate() != null) {
                ps.setDate(4, Date.valueOf(author.getBirthdate()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setString(5, author.getCountry());
            return ps;
        }, keyHolder);

        // Получаем ID из ключа
        Number key = (Number) keyHolder.getKeys().get("id");
        author.setId(key.longValue());
        log.info("Автор сохранен с id {}", author.getId());
        return author;
    }

    @Override
    public Author update(Author author) {
        log.debug("Обновление автора с id {}", author.getId());
        String sql = "UPDATE authors SET first_name = ?, last_name = ?, bio = ?, birthdate = ?, country = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                author.getFirstName(),
                author.getLastName(),
                author.getBio(),
                author.getBirthdate() != null ? Date.valueOf(author.getBirthdate()) : null,
                author.getCountry(),
                author.getId());

        if (rowsAffected == 0) {
            throw new RuntimeException("Не удалось обновить автора с ID: " + author.getId());
        }

        log.info("Автор с id {} успешно обновлен", author.getId());
        return author;
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted > 0) {
            log.info("Автор с id {} удален", id);
            return true;
        }
        log.warn("Автор с id {} не найден для удаления", id);
        return false;
    }

    @Override
    public boolean addBookToAuthor(long authorId, long bookId) {
        log.debug("Добавление книги {} автору {}", bookId, authorId);
        String sql = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        return jdbcTemplate.update(sql, bookId, authorId) > 0;
    }

    @Override
    public boolean removeBookFromAuthor(long authorId, long bookId) {
        log.debug("Удаление книги {} у автора {}", bookId, authorId);
        String sql = "DELETE FROM book_authors WHERE book_id = ? AND author_id = ?";
        return jdbcTemplate.update(sql, bookId, authorId) > 0;
    }
}
