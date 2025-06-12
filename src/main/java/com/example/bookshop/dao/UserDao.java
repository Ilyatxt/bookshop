package com.example.bookshop.dao;

import com.example.bookshop.model.Role;
import com.example.bookshop.model.User;

import java.util.List;
import java.util.Optional;

/**
 * DAO интерфейс для работы с пользователями
 */
public interface UserDao {

    /**
     * Получить пользователя по ID
     *
     * @param id идентификатор пользователя
     * @return пользователь или пустое значение, если пользователь не найден
     */
    Optional<User> findById(long id);

    /**
     * Получить пользователя по email
     *
     * @param email email пользователя
     * @return пользователь или пустое значение, если пользователь не найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Получить пользователя по имени пользователя
     *
     * @param username имя пользователя
     * @return пользователь или пустое значение, если пользователь не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Получить всех пользователей
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Сохранить пользователя
     *
     * @param user пользователь для сохранения
     * @return сохраненный пользователь
     */
    User save(User user);

    /**
     * Обновление роли пользователя
     *
     * @param userId идентификатор пользователя
     * @param role новая роль
     */
    void updateUserRole(long userId, Role role);

    /**
     * Удалить пользователя по ID
     *
     * @param id идентификатор пользователя для удаления
     */
    void delete(long id);
}
