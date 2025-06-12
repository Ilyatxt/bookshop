package com.example.bookshop.service;

import com.example.bookshop.model.OrderEntry;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с позициями заказа
 */
public interface OrderEntryService {

    /**
     * Найти позиции по ID заказа
     * @param orderId идентификатор заказа
     * @return список позиций заказа
     */
    List<OrderEntry> getOrderEntriesByOrderId(long orderId);

    /**
     * Найти позицию заказа по ID
     * @param id идентификатор позиции заказа
     * @return позиция заказа или пустой Optional, если позиция не найдена
     */
    Optional<OrderEntry> getOrderEntryById(long id);

    /**
     * Создать новую позицию заказа
     * @param orderEntry данные позиции заказа
     * @return созданная позиция заказа с присвоенным ID
     */
    OrderEntry createOrderEntry(OrderEntry orderEntry);

    /**
     * Сохранить несколько позиций заказа
     * @param orderEntries список позиций заказа для сохранения
     * @return список сохраненных позиций с присвоенными ID
     */
    List<OrderEntry> createOrderEntries(List<OrderEntry> orderEntries);

    /**
     * Обновить существующую позицию заказа
     * @param orderEntry обновленные данные позиции заказа
     * @return обновленная позиция заказа
     */
    OrderEntry updateOrderEntry(OrderEntry orderEntry);

    /**
     * Удалить позицию заказа по ID
     * @param id идентификатор позиции заказа
     * @return true, если позиция успешно удалена, иначе false
     */
    boolean deleteOrderEntry(long id);

    /**
     * Удалить все позиции заказа по ID заказа
     * @param orderId идентификатор заказа
     * @return количество удаленных позиций
     */
    int deleteOrderEntriesByOrderId(long orderId);
}
