package com.example.bookshop.service.impl;

import com.example.bookshop.dao.UserDao;
import com.example.bookshop.model.User;
import com.example.bookshop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис для работы с пользователями
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        log.info("UserServiceImpl инициализирован");
    }

    /**
     * Поиск пользователя по email
     */
    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("Поиск пользователя по email: {}", email);
        Optional<User> user = userDao.findByEmail(email);
        if (user.isPresent()) {
            log.debug("Пользователь с email {} найден: {}", email, user.get().getId());
        } else {
            log.debug("Пользователь с email {} не найден", email);
        }
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Поиск пользователя по username: {}", username);
        Optional<User> user = userDao.findByUsername(username);
        if (user.isPresent()) {
            log.debug("Пользователь {} найден", username);
        } else {
            log.debug("Пользователь {} не найден", username);
        }
        return user;
    }

    /**
     * Регистрация нового пользователя
     */
    @Override
    public User register(User user) {
        log.info("Попытка регистрации нового пользователя с email: {}", user.getEmail());

        if (userDao.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Регистрация не удалась: пользователь с email {} уже существует", user.getEmail());
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setCreatedAt(LocalDateTime.now()); //фасад
        User savedUser = userDao.save(user);
        log.info("Пользователь успешно зарегистрирован: ID={}, email={}", savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }

    /**
     * Обновление профиля пользователя (без смены пароля)
     */
    @Override
    public void updateProfile(User user) {
        log.info("Попытка обновления профиля пользователя: ID={}", user.getId());

        Optional<User> existing = userDao.findById(user.getId());
        if (existing.isEmpty()) {
            log.warn("Обновление профиля не удалось: пользователь с ID={} не найден", user.getId());
            throw new IllegalArgumentException("Пользователь не найден");
        }

        // Используем старый хэш пароля, чтобы не перезаписать случайно
        user.setPasswordHash(existing.get().getPasswordHash());
        userDao.save(user);
        log.info("Профиль пользователя успешно обновлен: ID={}, email={}", user.getId(), user.getEmail());
    }

    /**
     * Смена пароля пользователя
     */
    @Override
    public void changePassword(User user, String newPassword) {
        log.info("Попытка смены пароля для пользователя: ID={}", user.getId());

        Optional<User> existing = userDao.findById(user.getId());
        if (existing.isEmpty()) {
            log.warn("Смена пароля не удалась: пользователь с ID={} не найден", user.getId());
            throw new IllegalArgumentException("Пользователь не найден");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userDao.save(user);
        log.info("Пароль успешно изменен для пользователя: ID={}", user.getId());
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public java.util.List<User> getUsersPage(int page, int size) {
        java.util.List<User> users = userDao.findAll();
        int from = Math.min(page * size, users.size());
        int to = Math.min(from + size, users.size());
        return users.subList(from, to);
    }

    @Override
    public long countUsers() {
        return userDao.findAll().size();
    }

    @Override
    public void deleteUser(long id) {
        userDao.delete(id);
    }

    @Override
    public void updateRole(long userId, com.example.bookshop.model.Role role) {
        userDao.updateUserRole(userId, role);
    }
}
