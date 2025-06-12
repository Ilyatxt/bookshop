package com.example.bookshop.controller;

import com.example.bookshop.facade.UserFacade;
import com.example.bookshop.model.User;
import com.example.bookshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для обработки аутентификации и регистрации пользователей
 */
@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final UserFacade userFacade;

    public AuthController(UserService userService, UserFacade userFacade) {
        this.userService = userService;
        this.userFacade = userFacade;
    }

    /**
     * Обработка GET-запроса на страницу входа
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            Model model,
            HttpServletRequest request) {

        log.debug("Запрос страницы входа. error={}, logout={}, expired={}", error, logout, expired);

        if (error != null) {
            // Получаем сообщение об ошибке из сессии
            Object exception = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            String errorMessage = exception != null ? exception.toString() : "Неверный логин или пароль";
            model.addAttribute("error", errorMessage);
            log.debug("Ошибка входа: {}", errorMessage);
        }

        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
            log.debug("Пользователь вышел из системы");
        }

        if (expired != null) {
            model.addAttribute("expired", "Ваша сессия истекла. Пожалуйста, войдите снова.");
            log.debug("Сессия пользователя истекла");
        }

        return "login";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    /**
     * Обработка POST-запроса на регистрацию пользователя
     */
    @PostMapping("/register")
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        try {
            userFacade.registerNewClient(username, email, password);
            redirectAttributes.addFlashAttribute("message", "Регистрация успешно завершена. Пожалуйста, войдите в систему.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // В случае ошибки (например, email уже существует)
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/users/register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "errors/access-denied";
    }
}
