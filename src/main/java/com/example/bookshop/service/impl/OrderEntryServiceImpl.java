package com.example.bookshop.service.impl;

import com.example.bookshop.dao.OrderEntryDao;
import com.example.bookshop.model.OrderEntry;
import com.example.bookshop.service.OrderEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с позициями заказа
 */
@Service
public class OrderEntryServiceImpl implements OrderEntryService {

    private static final Logger log = LoggerFactory.getLogger(OrderEntryServiceImpl.class);
    private final OrderEntryDao orderEntryDao;

    public OrderEntryServiceImpl(OrderEntryDao orderEntryDao) {
        this.orderEntryDao = orderEntryDao;
    }

    @Override
    public List<OrderEntry> getOrderEntriesByOrderId(long orderId) {
        log.debug("Получение позиций для заказа с id: {}", orderId);
        return orderEntryDao.findByOrderId(orderId);
    }

    @Override
    public Optional<OrderEntry> getOrderEntryById(long id) {
        log.debug("Поиск позиции заказа по id: {}", id);
        return orderEntryDao.findById(id);
    }

    @Override
    @Transactional
    public OrderEntry createOrderEntry(OrderEntry orderEntry) {
        log.info("Создание новой позиции заказа");
        return orderEntryDao.save(orderEntry);
    }

    @Override
    @Transactional
    public List<OrderEntry> createOrderEntries(List<OrderEntry> orderEntries) {
        log.info("Создание {} позиций заказа", orderEntries.size());
        return orderEntryDao.saveAll(orderEntries);
    }

    @Override
    @Transactional
    public OrderEntry updateOrderEntry(OrderEntry orderEntry) {
        log.info("Обновление позиции заказа с id: {}", orderEntry.getId());
        return orderEntryDao.update(orderEntry);
    }

    @Override
    @Transactional
    public boolean deleteOrderEntry(long id) {
        log.info("Удаление позиции заказа с id: {}", id);
        return orderEntryDao.deleteById(id);
    }

    @Override
    @Transactional
    public int deleteOrderEntriesByOrderId(long orderId) {
        log.info("Удаление всех позиций для заказа с id: {}", orderId);
        return orderEntryDao.deleteByOrderId(orderId);
    }
}
