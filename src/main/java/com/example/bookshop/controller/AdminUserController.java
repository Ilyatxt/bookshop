package com.example.bookshop.controller;

import com.example.bookshop.model.Role;
import com.example.bookshop.model.User;
import com.example.bookshop.service.OrderService;
import com.example.bookshop.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Контроллер страниц управления пользователями для администраторов
 */
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final OrderService orderService;

    public AdminUserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    /**
     * Список пользователей
     */
    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        List<User> users = userService.getUsersPage(page, size);
        long total = userService.countUsers();
        long totalPages = (total + size - 1) / size;
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        return "users/admin-list";
    }

    /**
     * Детали пользователя
     */
    @GetMapping("/{id}")
    public String userDetails(@PathVariable long id, Model model, RedirectAttributes redirect) {
        return userService.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("orders", orderService.getOrdersByUserId(id));
                    return "users/details";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Пользователь не найден");
                    return "redirect:/admin/users";
                });
    }

    /**
     * Форма создания нового пользователя
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "users/form";
    }

    /**
     * Создание пользователя
     */
    @PostMapping
    public String createUser(@ModelAttribute User user, @RequestParam Role role) {
        user.setRole(role);
        userService.register(user);
        return "redirect:/admin/users";
    }

    /**
     * Форма редактирования пользователя
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable long id, Model model, RedirectAttributes redirect) {
        return userService.findById(id)
                .map(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute("roles", Role.values());
                    return "users/form";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Пользователь не найден");
                    return "redirect:/admin/users";
                });
    }

    /**
     * Обновление пользователя
     */
    @PostMapping("/{id}")
    public String updateUser(@PathVariable long id, @ModelAttribute User user, @RequestParam Role role) {
        user.setId(id);
        user.setRole(role);
        userService.updateProfile(user);
        return "redirect:/admin/users/" + id;
    }

    /**
     * Удаление пользователя
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    /**
     * Форма смены пароля
     */
    @GetMapping("/{id}/password")
    public String showPasswordForm(@PathVariable long id, Model model, RedirectAttributes redirect) {
        return userService.findById(id)
                .map(u -> {
                    model.addAttribute("user", u);
                    return "users/change-password";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Пользователь не найден");
                    return "redirect:/admin/users";
                });
    }

    /**
     * Изменение пароля
     */
    @PostMapping("/{id}/password")
    public String changePassword(@PathVariable long id, @RequestParam String newPassword) {
        userService.findById(id).ifPresent(u -> userService.changePassword(u, newPassword));
        return "redirect:/admin/users/" + id;
    }
}
