package com.example.bookshop.dao.impl;

import com.example.bookshop.dao.UserDao;
import com.example.bookshop.model.Role;
import com.example.bookshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация DAO для работы с пользователями
 */
@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.valueOf(rs.getString("role")));
        Timestamp createdAt = rs.getTimestamp("created_at");
        user.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return user;
    };

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
         * {@inheritDoc}
         */
        @Override
    public Optional<User> findById(long id) {
        log.debug("Поиск пользователя по id {}", id);
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Поиск пользователя по email {}", email);
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Поиск пользователя по username {}", username);
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() {
        log.debug("Получение списка всех пользователей");
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);
        log.debug("Найдено {} пользователей", users.size());
        return users;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        if (user.getId() > 0) {
            return update(user);
        } else {
            return insert(user);
        }
    }

    /**
     * Добавление нового пользователя
     */
    private User insert(User user) {
        log.debug("Создание пользователя {}", user.getUsername());
        String sql = "INSERT INTO users (username, email, password_hash, role, created_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());

            LocalDateTime createdAt = user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(createdAt));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        log.info("Пользователь создан с id {}", user.getId());
        return user;
    }

    /**
     * Обновление существующего пользователя
     */
    private User update(User user) {
        log.debug("Обновление пользователя с id {}", user.getId());
        String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, role = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                user.getId());
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserRole(long userId, Role role) {
        log.debug("Изменение роли пользователя {} на {}", userId, role);
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        jdbcTemplate.update(sql, role.name(), userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(long id) {
        log.debug("Удаление пользователя с id {}", id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
