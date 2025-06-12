package com.example.bookshop.dao;

import com.example.bookshop.model.OrderEntry;

import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с позициями заказа
 */
public interface OrderEntryDao {

    /**
     * Найти все позиции заказа по ID заказа
     *
     * @param orderId идентификатор заказа
     * @return список позиций заказа
     */
    List<OrderEntry> findByOrderId(long orderId);

    /**
     * Найти позицию заказа по ID
     *
     * @param id идентификатор позиции заказа
     * @return позиция заказа или пустой Optional, если позиция не найдена
     */
    Optional<OrderEntry> findById(long id);

    /**
     * Сохранить новую позицию заказа
     *
     * @param orderEntry данные позиции заказа для сохранения
     * @return сохраненная позиция заказа с установленным ID
     */
    OrderEntry save(OrderEntry orderEntry);

    /**
     * Сохранить список позиций заказа
     *
     * @param orderEntries список позиций заказа для сохранения
     * @return список сохраненных позиций заказа с установленными ID
     */
    List<OrderEntry> saveAll(List<OrderEntry> orderEntries);

    /**
     * Обновить существующую позицию заказа
     *
     * @param orderEntry данные позиции заказа для обновления
     * @return обновленная позиция заказа
     */
    OrderEntry update(OrderEntry orderEntry);

    /**
     * Удалить позицию заказа по ID
     *
     * @param id идентификатор позиции заказа для удаления
     * @return true если позиция была удалена, false если позиция не найдена
     */
    boolean deleteById(long id);

    /**
     * Удалить все позиции заказа по ID заказа
     *
     * @param orderId идентификатор заказа
     * @return количество удаленных позиций
     */
    int deleteByOrderId(long orderId);
}