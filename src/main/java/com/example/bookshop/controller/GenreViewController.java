package com.example.bookshop.controller;

import com.example.bookshop.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * MVC контроллер для страниц жанров
 */
@Controller
@RequestMapping("/genres")
public class GenreViewController {

    private final BookService bookService;

    public GenreViewController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Страница со списком всех жанров
     */
    @GetMapping
    public String getAllGenres(Model model) {
        List<String> genres = bookService.getAllGenres();
        model.addAttribute("genres", genres);
        return "genres/list";
    }
}
