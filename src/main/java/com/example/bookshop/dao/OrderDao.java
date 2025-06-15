package com.example.bookshop.dao;

import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с заказами
 */
public interface OrderDao {

    /**
     * Найти все заказы
     *
     * @return список всех заказов
     */
    List<Order> findAll();

    /**
     * Найти все заказы с пагинацией
     *
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список заказов для указанной страницы
     */
    List<Order> findAll(int pageNumber, int pageSize);

    /**
     * Получить общее количество заказов
     *
     * @return количество заказов
     */
    long countAll();

    /**
     * Найти заказ по ID
     *
     * @param id идентификатор заказа
     * @return заказ или пустой Optional, если заказ не найден
     */
    Optional<Order> findById(long id);

    /**
     * Найти все заказы пользователя
     *
     * @param userId идентификатор пользователя
     * @return список заказов пользователя
     */
    List<Order> findByUserId(long userId);

    /**
     * Найти заказы пользователя по статусам
     *
     * @param userId   идентификатор пользователя
     * @param statuses список статусов
     * @return список заказов пользователя с указанными статусами
     */
    List<Order> findByUserIdAndStatusIn(long userId, List<OrderStatus> statuses);

    /**
     * Найти заказ по коду заказа
     *
     * @param orderCode код заказа
     * @return заказ или пустой Optional, если заказ не найден
     */
    Optional<Order> findByOrderCode(String orderCode);

    /**
     * Найти заказы за определенный период
     *
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return список заказов за указанный период
     */
    List<Order> findByDateRange(OffsetDateTime startDate, OffsetDateTime endDate);

    /**
     * Сохранить новый заказ
     *
     * @param order данные заказа для сохранения
     * @return сохраненный заказ с установленным ID
     */
    Order save(Order order);

    /**
     * Обновить существующий заказ
     *
     * @param order данные заказа для обновления
     * @return обновленный заказ
     */
    Order update(Order order);

    /**
     * Обновить статус заказа
     *
     * @param id     идентификатор заказа
     * @param status новый статус
     * @return количество обновленных записей
     */
    int updateStatus(long id, OrderStatus status);

    /**
     * Удалить заказ по ID
     *
     * @param id идентификатор заказа для удаления
     * @return true если заказ был удален, false если заказ не найден
     */
    boolean deleteById(long id);
}