package com.example.bookshop.controller;

import com.example.bookshop.model.User;
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

    @Autowired
    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображение формы регистрации
     */

    /**
     * Профиль пользователя
     */
    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        // В реальном приложении здесь будет получение текущего пользователя из сессии
        // Например: User user = getCurrentUser();

        // Временная заглушка - потом заменить на получение реального пользователя
        User dummyUser = new User();
        dummyUser.setUsername("Текущий пользователь");

        model.addAttribute("user", dummyUser);
        return "users/profile";
    }

    /**
     * Форма изменения пароля
     */
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "users/change-password";
    }

    /**
     * Обработка формы изменения пароля
     */
    @PostMapping("/change-password")
    public String processChangePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) {

        try {
            // Получение текущего пользователя - в реальном приложении из сессии
            // User currentUser = getCurrentUser();

            // Временная заглушка - потом заменить на получение реального пользователя
            User dummyUser = new User();
            dummyUser.setId(1L);

            // Изменение пароля
            userService.changePassword(dummyUser, newPassword);

            redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен");
            return "redirect:/users/profile";

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", "Ошибка при смене пароля: " + e.getMessage());
            return "redirect:/users/change-password";
        }
    }
}
