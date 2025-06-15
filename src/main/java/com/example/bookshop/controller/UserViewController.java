package com.example.bookshop.controller;

import com.example.bookshop.model.User;
import com.example.bookshop.service.OrderService;
import com.example.bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для работы с пользователями
 */
@Controller("/usersxx")
public class UserViewController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserViewController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    /**
     * Отображение формы регистрации
     */

    /**
     * Профиль пользователя
     */
    @GetMapping("/profile")
    public String showUserProfile(Model model, java.security.Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        return userService.findByUsername(principal.getName())
                .map(u -> {
                    model.addAttribute("user", u);
                    model.addAttribute("orders", orderService.getOrdersByUserId(u.getId()));
                    return "users/profile";
                })
                .orElse("redirect:/login");
    }

    /**
     * Форма изменения пароля
     */
    @GetMapping("/profile/password")
    public String showChangePasswordForm() {
        return "users/change-password-user";
    }

    /**
     * Обработка формы изменения пароля
     */
    @PostMapping("/profile/password")
    public String processChangePassword(
            @RequestParam("newPassword") String newPassword,
            java.security.Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            return userService.findByUsername(principal.getName())
                    .map(u -> {
                        userService.changePassword(u, newPassword);
                        redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен");
                        return "redirect:/profile";
                    })
                    .orElse("redirect:/login");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при смене пароля: " + e.getMessage());
            return "redirect:/profile/password";
        }
    }
}
