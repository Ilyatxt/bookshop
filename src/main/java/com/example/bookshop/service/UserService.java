package com.example.bookshop.service;

import com.example.bookshop.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    User register(User user);

    void updateProfile(User user);

    void changePassword(User user, String newPassword);

    /**
     * Получить пользователя по ID
     */
    Optional<User> findById(long id);

    /**
     * Получить страницу пользователей
     */
    java.util.List<User> getUsersPage(int page, int size);

    /**
     * Количество пользователей
     */
    long countUsers();

    /**
     * Удалить пользователя
     */
    void deleteUser(long id);

    /**
     * Обновить роль пользователя
     */
    void updateRole(long userId, com.example.bookshop.model.Role role);

}
