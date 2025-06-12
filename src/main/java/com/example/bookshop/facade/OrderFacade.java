package com.example.bookshop.facade;

import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderEntry;
import com.example.bookshop.model.OrderStatus;
import com.example.bookshop.model.Book;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.OrderEntryService;
import com.example.bookshop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Фасад для работы с заказами и их позициями
 * Объединяет функциональность сервисов заказов и позиций заказа
 */
@Component
public class OrderFacade {

    private static final Logger log = LoggerFactory.getLogger(OrderFacade.class);
    private final OrderService orderService;
    private final OrderEntryService orderEntryService;
    private final BookService bookService;

    public OrderFacade(OrderService orderService, OrderEntryService orderEntryService, BookService bookService) {
        this.orderService = orderService;
        this.orderEntryService = orderEntryService;
        this.bookService = bookService;
    }

    /**
     * Получить список всех заказов
     * @return список заказов
     */
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Получить список заказов с пагинацией
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список заказов на указанной странице
     */
    public List<Order> getOrdersPage(int pageNumber, int pageSize) {
        return orderService.getAllOrders(pageNumber, pageSize);
    }

    /**
     * Получить общее количество заказов
     * @return количество заказов
     */
    public long getOrdersCount() {
        return orderService.countAllOrders();
    }

    /**
     * Получить заказ по идентификатору с его позициями
     * @param orderId идентификатор заказа
     * @return заказ с позициями или пустой Optional
     */
    public Optional<Order> getOrderWithEntries(long orderId) {
        Optional<Order> orderOptional = orderService.getOrderById(orderId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderEntry> entries = orderEntryService.getOrderEntriesByOrderId(orderId);
            order.setEntries(entries);
            return Optional.of(order);
        }

        return Optional.empty();
    }

    /**
     * Получить заказ по коду с его позициями
     * @param orderCode код заказа
     * @return заказ с позициями или пустой Optional
     */
    public Optional<Order> getOrderByCodeWithEntries(String orderCode) {
        Optional<Order> orderOptional = orderService.getOrderByCode(orderCode);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderEntry> entries = orderEntryService.getOrderEntriesByOrderId(order.getId());
            order.setEntries(entries);
            return Optional.of(order);
        }

        return Optional.empty();
    }

    /**
     * Получить список заказов пользователя
     * @param userId идентификатор пользователя
     * @return список заказов пользователя
     */
    public List<Order> getUserOrders(long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    /**
     * Получить список заказов за период
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список заказов за указанный период
     */
    public List<Order> getOrdersByPeriod(OffsetDateTime startDate, OffsetDateTime endDate) {
        return orderService.getOrdersByDateRange(startDate, endDate);
    }

    /**
     * Создать новый заказ с позициями
     * @param order заказ с позициями
     * @return созданный заказ с позициями и присвоенными ID
     */
    @Transactional
    public Order createOrderWithEntries(Order order) {
        log.info("Создание нового заказа с позициями");

        // Сохраняем заказ
        Order savedOrder = orderService.createOrder(order);
        long orderId = savedOrder.getId();

        // Устанавливаем ID заказа для всех позиций
        List<OrderEntry> entries = order.getEntries();
        if (entries != null && !entries.isEmpty()) {
            entries.forEach(entry -> entry.setOrderId(orderId));

            // Сохраняем позиции заказа
            List<OrderEntry> savedEntries = orderEntryService.createOrderEntries(entries);
            savedOrder.setEntries(savedEntries);
        }

        return savedOrder;
    }

    /**
     * Обновить существующий заказ с позициями
     * @param order обновленный заказ с позициями
     * @return обновленный заказ
     */
    @Transactional
    public Order updateOrderWithEntries(Order order) {
        log.info("Обновление заказа с id: {} и его позиций", order.getId());

        // Обновляем заказ
        Order updatedOrder = orderService.updateOrder(order);
        long orderId = updatedOrder.getId();

        // Удаляем существующие позиции
        orderEntryService.deleteOrderEntriesByOrderId(orderId);

        // Сохраняем новые позиции
        List<OrderEntry> entries = order.getEntries();
        if (entries != null && !entries.isEmpty()) {
            entries.forEach(entry -> entry.setOrderId(orderId));
            List<OrderEntry> savedEntries = orderEntryService.createOrderEntries(entries);
            updatedOrder.setEntries(savedEntries);
        }

        return updatedOrder;
    }

    /**
     * Добавить книгу в заказ пользователя. Создает новый заказ, если актуального нет.
     */
    @Transactional
    public Order addBookToUserOrder(long userId, long bookId, int quantity) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        Order order = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.PAID && o.getStatus() != OrderStatus.DECLINED)
                .findFirst()
                .orElse(null);

        Book book = bookService.getBookById(bookId);

        OrderEntry entry = new OrderEntry();
        entry.setBookId(bookId);
        entry.setQuantity(quantity);
        entry.setUnitPrice(book.getPrice().doubleValue());

        if (order == null) {
            order = new Order();
            order.setUserId(userId);
            order.setOrderDate(OffsetDateTime.now());
            order.setOrderCode("ORD-" + System.currentTimeMillis());
            order.setCurrency(book.getCurrency());
            order.setTotalPrice(book.getPrice().multiply(java.math.BigDecimal.valueOf(quantity)));
            order.setEntries(java.util.List.of(entry));
            return createOrderWithEntries(order);
        } else {
            entry.setOrderId(order.getId());
            orderEntryService.createOrderEntry(entry);
            order.setTotalPrice(order.getTotalPrice().add(book.getPrice().multiply(java.math.BigDecimal.valueOf(quantity))));
            orderService.updateOrder(order);
            return order;
        }
    }

    /**
     * Удалить заказ со всеми позициями
     * @param orderId идентификатор заказа
     * @return true если заказ успешно удален, иначе false
     */
    @Transactional
    public boolean deleteOrder(long orderId) {
        log.info("Удаление заказа с id: {} и всех его позиций", orderId);
        return orderService.deleteOrder(orderId);
    }
}
