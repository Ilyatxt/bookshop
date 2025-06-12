package com.example.bookshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для домашней страницы
 */
@Controller
public class HomeController {

    /**
     * Перенаправление с корневого URL на страницу книг
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }
}
