package com.example.bookshop.controller;

import com.example.bookshop.facade.OrderFacade;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderEntry;
import com.example.bookshop.model.OrderStatus;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.User;
import com.example.bookshop.service.BookService;
import com.example.bookshop.dao.UserDao;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    private final BookService bookService;
    private final UserDao userDao;

    public OrderViewController(OrderFacade orderFacade, BookService bookService, UserDao userDao) {
        this.orderFacade = orderFacade;
        this.bookService = bookService;
        this.userDao = userDao;
    }

    /**
     * Отображение списка заказов с пагинацией
     */
    @GetMapping
    public String listOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
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
     * Заказы текущего пользователя со статусом NEW или IN_PROCESS
     */
    @GetMapping("/my")
    public String listCurrentUserOrders(Principal principal, Model model) {
        User user = userDao.findByUsername(principal.getName()).orElseThrow();
        List<Order> orders = orderFacade.getUserOrdersByStatuses(user.getId(), List.of(OrderStatus.NEW, OrderStatus.IN_PROCESS));
        model.addAttribute("orders", orders);
        model.addAttribute("userId", user.getId());
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
            var updated = orderFacade.updateOrderWithEntriesIfExists(orderId, order);
            if (updated.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Заказ с ID " + orderId + " не найден");
                return "redirect:/orders";
            }

            Order updatedOrder = updated.get();
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
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            Model model) {
        List<Order> orders = orderFacade.searchOrdersByPeriod(startDate, endDate);
        model.addAttribute("orders", orders);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "orders/search-results";
    }

    /**
     * Страница оформления покупки книги
     */
    @GetMapping("/buy/{bookId}")
    public String showBuyForm(@PathVariable(name = "bookId") long bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        model.addAttribute("book", book);
        model.addAttribute("quantityOptions", List.of(1, 2, 3, 4, 5));
        model.addAttribute("orderEntry", new OrderEntry());
        return "orders/buy";
    }

    /**
     * Добавление позиции в заказ пользователя
     */
    @PostMapping("/buy")
    public String processBuy(@RequestParam("bookId") long bookId,
                             @RequestParam("quantity") int quantity,
                             java.security.Principal principal,
                             RedirectAttributes redirectAttributes) {
        User user = userDao.findByUsername(principal.getName()).orElseThrow();
        orderFacade.addBookToUserOrder(user.getId(), bookId, quantity);
        redirectAttributes.addFlashAttribute("success", "Книга добавлена в заказ");
        return "redirect:/books/user/" + bookId;
    }

    /**
     * Оплата заказа
     */
    @PostMapping("/{orderId}/pay")
    public String payOrder(@PathVariable(name = "orderId") long orderId, RedirectAttributes redirectAttributes) {
        orderFacade.updateOrderStatus(orderId, OrderStatus.PAID);
        redirectAttributes.addFlashAttribute("success", "Заказ оплачен");
        return "redirect:/orders/" + orderId;
    }

    /**
     * Отмена заказа
     */
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable(name = "orderId") long orderId, RedirectAttributes redirectAttributes) {
        orderFacade.updateOrderStatus(orderId, OrderStatus.DECLINED);
        redirectAttributes.addFlashAttribute("success", "Заказ отменен");
        return "redirect:/orders/" + orderId;
    }
}
