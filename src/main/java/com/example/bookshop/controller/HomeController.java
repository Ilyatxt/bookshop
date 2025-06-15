package com.example.bookshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Контроллер для домашней страницы
 */
@Controller
public class HomeController {

    /**
     * Перенаправление с корневого URL на страницу книг
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/users";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MODERATOR"))) {
            return "redirect:/books";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return "redirect:/books/user/books";
        }

        return "redirect:/login";
    }
}
