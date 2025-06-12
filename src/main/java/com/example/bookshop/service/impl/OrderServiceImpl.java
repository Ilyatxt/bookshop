package com.example.bookshop.service.impl;

import com.example.bookshop.dao.OrderDao;
import com.example.bookshop.dao.OrderEntryDao;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderStatus;
import com.example.bookshop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с заказами
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderDao orderDao;
    private final OrderEntryDao orderEntryDao;

    public OrderServiceImpl(OrderDao orderDao, OrderEntryDao orderEntryDao) {
        this.orderDao = orderDao;
        this.orderEntryDao = orderEntryDao;
    }

    @Override
    public List<Order> getAllOrders() {
        log.debug("Получение всех заказов");
        return orderDao.findAll();
    }

    @Override
    public List<Order> getAllOrders(int pageNumber, int pageSize) {
        log.debug("Получение страницы заказов: страница {}, размер {}", pageNumber, pageSize);
        return orderDao.findAll(pageNumber, pageSize);
    }

    @Override
    public long countAllOrders() {
        log.debug("Подсчет общего количества заказов");
        return orderDao.countAll();
    }

    @Override
    public Optional<Order> getOrderById(long id) {
        log.debug("Поиск заказа по id: {}", id);
        return orderDao.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(long userId) {
        log.debug("Поиск заказов пользователя с id: {}", userId);
        return orderDao.findByUserId(userId);
    }

    @Override
    public Optional<Order> getOrderByCode(String orderCode) {
        log.debug("Поиск заказа по коду: {}", orderCode);
        return orderDao.findByOrderCode(orderCode);
    }

    @Override
    public List<Order> getOrdersByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.debug("Поиск заказов в диапазоне дат от {} до {}", startDate, endDate);
        return orderDao.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        log.info("Создание нового заказа");
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.NEW);
        }
        return orderDao.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
        log.info("Обновление заказа с id: {}", order.getId());
        return orderDao.update(order);
    }

    @Override
    @Transactional
    public boolean deleteOrder(long id) {
        log.info("Удаление заказа с id: {}", id);
        // Сначала удаляем все позиции заказа
        orderEntryDao.deleteByOrderId(id);
        // Затем удаляем сам заказ
        return orderDao.deleteById(id);
    }
}
