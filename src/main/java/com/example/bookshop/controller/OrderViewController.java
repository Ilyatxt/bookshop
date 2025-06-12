package com.example.bookshop.controller;

import com.example.bookshop.facade.OrderFacade;
import com.example.bookshop.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для работы с заказами через веб-интерфейс
 */
@Controller
@RequestMapping("/orders")
public class OrderViewController {

    private static final Logger log = LoggerFactory.getLogger(OrderViewController.class);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private final OrderFacade orderFacade;

    public OrderViewController(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    /**
     * Отображение списка заказов с пагинацией
     */
    @GetMapping
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        List<Order> orders = orderFacade.getOrdersPage(page, size);
        long totalOrders = orderFacade.getOrdersCount();
        long totalPages = (totalOrders + size - 1) / size;

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

        return "orders/list";
    }

    /**
     * Отображение информации о конкретном заказе
     */
    @GetMapping("/{orderId}")
    public String viewOrder(@PathVariable(name = "orderId") long orderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Order> orderOptional = orderFacade.getOrderWithEntries(orderId);

        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
            return "orders/details";
        } else {
            redirectAttributes.addFlashAttribute("error", "Заказ с ID " + orderId + " не найден");
            return "redirect:/orders";
        }
    }

    /**
     * Отображение заказа по коду заказа
     */
    @GetMapping("/code/{orderCode}")
    public String viewOrderByCode(@PathVariable(name = "orderCode") String orderCode, Model model, RedirectAttributes redirectAttributes) {
        Optional<Order> orderOptional = orderFacade.getOrderByCodeWithEntries(orderCode);

        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
            return "orders/details";
        } else {
            redirectAttributes.addFlashAttribute("error", "Заказ с кодом " + orderCode + " не найден");
            return "redirect:/orders";
        }
    }

    /**
     * Отображение заказов конкретного пользователя
     */
    @GetMapping("/user/{userId}")
    public String listUserOrders(@PathVariable(name = "userId") long userId, Model model) {
        List<Order> userOrders = orderFacade.getUserOrders(userId);
        model.addAttribute("orders", userOrders);
        model.addAttribute("userId", userId);
        return "orders/user-orders";
    }

    /**
     * Форма для создания нового заказа
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        return "orders/form";
    }

    /**
     * Обработка формы создания заказа
     */
    @PostMapping("/create")
    public String createOrder(@ModelAttribute Order order, RedirectAttributes redirectAttributes) {
        try {
            // Установка текущей даты заказа, если она не указана
            if (order.getOrderDate() == null) {
                order.setOrderDate(OffsetDateTime.now());
            }

            Order createdOrder = orderFacade.createOrderWithEntries(order);
            redirectAttributes.addFlashAttribute("success", "Заказ успешно создан");
            return "redirect:/orders/" + createdOrder.getId();
        } catch (Exception e) {
            log.error("Ошибка при создании заказа", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании заказа: " + e.getMessage());
            return "redirect:/orders/create";
        }
    }

    /**
     * Форма для редактирования заказа
     */
    @GetMapping("/{orderId}/edit")
    public String showEditForm(@PathVariable(name = "orderId") long orderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Order> orderOptional = orderFacade.getOrderWithEntries(orderId);

        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
            return "orders/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Заказ с ID " + orderId + " не найден");
            return "redirect:/orders";
        }
    }

    /**
     * Обработка формы редактирования заказа
     */
    @PostMapping("/{orderId}/edit")
    public String updateOrder(@PathVariable(name = "orderId") long orderId, @ModelAttribute Order order, RedirectAttributes redirectAttributes) {
        try {
            // Проверяем существование заказа
            if (!orderFacade.getOrderWithEntries(orderId).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Заказ с ID " + orderId + " не найден");
                return "redirect:/orders";
            }

            // Устанавливаем ID заказа
            order.setId(orderId);

            Order updatedOrder = orderFacade.updateOrderWithEntries(order);
            redirectAttributes.addFlashAttribute("success", "Заказ успешно обновлен");
            return "redirect:/orders/" + updatedOrder.getId();
        } catch (Exception e) {
            log.error("Ошибка при обновлении заказа", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении заказа: " + e.getMessage());
            return "redirect:/orders/" + orderId + "/edit";
        }
    }

    /**
     * Удаление заказа
     */
    @PostMapping("/{orderId}/delete")
    public String deleteOrder(@PathVariable(name = "orderId") long orderId, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = orderFacade.deleteOrder(orderId);

            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Заказ успешно удален");
            } else {
                redirectAttributes.addFlashAttribute("error", "Заказ с ID " + orderId + " не найден");
            }

            return "redirect:/orders";
        } catch (Exception e) {
            log.error("Ошибка при удалении заказа", e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении заказа: " + e.getMessage());
            return "redirect:/orders";
        }
    }

    /**
     * Поиск заказов за период
     */
    @GetMapping("/search")
    public String searchOrdersByPeriod(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {

        // Если даты не указаны, устанавливаем диапазон за последние 30 дней
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Преобразуем LocalDate в OffsetDateTime
        OffsetDateTime startDateTime = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDateTime = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<Order> orders = orderFacade.getOrdersByPeriod(startDateTime, endDateTime);

        model.addAttribute("orders", orders);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "orders/search-results";
    }
}
