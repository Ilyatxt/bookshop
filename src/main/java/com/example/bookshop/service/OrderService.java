package com.example.bookshop.service;

import com.example.bookshop.model.Order;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с заказами
 */
public interface OrderService {

    /**
     * Получить все заказы
     * @return список всех заказов
     */
    List<Order> getAllOrders();

    /**
     * Получить заказы с пагинацией
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список заказов на указанной странице
     */
    List<Order> getAllOrders(int pageNumber, int pageSize);

    /**
     * Получить общее количество заказов
     * @return количество заказов
     */
    long countAllOrders();

    /**
     * Найти заказ по ID
     * @param id идентификатор заказа
     * @return заказ или пустой Optional, если заказ не найден
     */
    Optional<Order> getOrderById(long id);

    /**
     * Найти заказы пользователя
     * @param userId идентификатор пользователя
     * @return список заказов пользователя
     */
    List<Order> getOrdersByUserId(long userId);

    /**
     * Найти заказы пользователя по статусам
     *
     * @param userId   идентификатор пользователя
     * @param statuses список статусов
     * @return список заказов пользователя с указанными статусами
     */
    List<Order> getOrdersByUserIdAndStatuses(long userId, java.util.List<com.example.bookshop.model.OrderStatus> statuses);

    /**
     * Найти заказ по коду заказа
     * @param orderCode код заказа
     * @return заказ или пустой Optional, если заказ не найден
     */
    Optional<Order> getOrderByCode(String orderCode);

    /**
     * Найти заказы в указанном диапазоне дат
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список заказов в указанном диапазоне
     */
    List<Order> getOrdersByDateRange(OffsetDateTime startDate, OffsetDateTime endDate);

    /**
     * Создать новый заказ
     * @param order данные заказа
     * @return созданный заказ с присвоенным ID
     */
    Order createOrder(Order order);

    /**
     * Обновить существующий заказ
     * @param order обновленные данные заказа
     * @return обновленный заказ
     */
    Order updateOrder(Order order);

    /**
     * Обновить статус заказа
     *
     * @param orderId идентификатор заказа
     * @param status  новый статус
     */
    void updateOrderStatus(long orderId, com.example.bookshop.model.OrderStatus status);

    /**
     * Удалить заказ по ID
     * @param id идентификатор заказа
     * @return true, если заказ успешно удален, иначе false
     */
    boolean deleteOrder(long id);
}
