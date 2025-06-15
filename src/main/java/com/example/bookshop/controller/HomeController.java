package com.example.bookshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Контроллер для домашней страницы
 */
@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    /**
     * Перенаправление с корневого URL на страницу книг
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        log.debug("Обработка запроса на домашнюю страницу");
        if (authentication == null) {
            return "redirect:/login";
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            log.debug("Пользователь с ролью ADMIN перенаправляется на страницу пользователей");
            return "redirect:/admin/users";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MODERATOR"))) {
            log.debug("Пользователь с ролью MODERATOR перенаправляется на книги");
            return "redirect:/books";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            log.debug("Пользователь с ролью USER перенаправляется на свои книги");
            return "redirect:/books/user/books";
        }

        log.debug("Неизвестная роль пользователя, перенаправление на login");
        return "redirect:/login";
    }
}
