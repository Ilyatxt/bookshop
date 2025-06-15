package com.example.bookshop.controller;

import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.model.Author;
import com.example.bookshop.service.AuthorService;
import com.example.bookshop.service.BookService;
import com.example.bookshop.facade.AuthorFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


/**
 * MVC контроллер для страниц авторов
 */
@Controller
@RequestMapping("/authors")
public class AuthorViewController {

    private static final Logger log = LoggerFactory.getLogger(AuthorViewController.class);

    private final AuthorService authorService;
    private final BookService bookService;
    private final AuthorFacade authorFacade;

    public AuthorViewController(AuthorService authorService, BookService bookService, AuthorFacade authorFacade) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.authorFacade = authorFacade;
    }

    /**
     * Страница всех авторов
     */
    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping
    public String getAllAuthors(@RequestParam(defaultValue = "0", name = "page") int page,
                                @RequestParam(defaultValue = "10", name = "size") int size,
                                Model model) {
        log.debug("Запрос списка авторов page={}, size={}", page, size);
        PageResponse<Author> authorPage = authorFacade.getAllAuthors(page, size);
        model.addAttribute("authorPage", authorPage);
        return "authors/list";
    }

    /**
     * Страница автора по ID
     */
    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/{id}")
    public String getAuthorById(@PathVariable(name = "id") long id, Model model) {
        log.debug("Запрос автора по id {}", id);
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        // Получаем книги автора
        model.addAttribute("books", bookService.getBooksByAuthorId(id));
        return "authors/details";
    }

    /**
     * Страница поиска авторов
     */
    @GetMapping("/search")
    public String searchAuthors(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            Model model) {
        PageResponse<Author> authorPage;
        if (name != null && !name.isEmpty()) {
            authorPage = authorFacade.searchAuthorsByName(name, page, size);
        } else {
            authorPage = authorFacade.getAllAuthors(page, size);
        }
        model.addAttribute("authorPage", authorPage);
        model.addAttribute("searchQuery", name);
        return "authors/search";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/authors")
    public String getAuthorsForUser(@RequestParam(defaultValue = "0", name = "page") int page,
                                    @RequestParam(defaultValue = "10", name = "size") int size,
                                    Model model) {
        PageResponse<Author> authorPage = authorFacade.getAllAuthors(page, size);
        model.addAttribute("authorPage", authorPage);
        return "authors/listForUser";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{id}")
    public String getAuthorForUser(@PathVariable(name = "id") long id, Model model) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        model.addAttribute("books", bookService.getBooksByAuthorId(id));
        return "authors/detailsForUser";
    }

    /**
     * Страница создания нового автора
     */
    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/form";
    }

    /**
     * Обработка создания нового автора
     */
    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping
    public String createAuthor(@ModelAttribute Author author) {
        log.debug("Создание автора {} {}", author.getFirstName(), author.getLastName());
        Author savedAuthor = authorService.createAuthor(author);
        return "redirect:/authors/" + savedAuthor.getId();
    }

    /**
     * Страница редактирования автора
     */
    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("author", authorService.getAuthorById(id));
        return "authors/form";
    }

    /**
     * Обработка обновления автора
     */
    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/{id}")
    public String updateAuthor(@PathVariable(name = "id") long id, @ModelAttribute Author author) {
        authorService.updateAuthor(id, author);
        return "redirect:/authors/" + id;
    }

    /**
     * Обработка удаления автора
     */
    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/{id}/delete")
    public String deleteAuthor(@PathVariable(name = "id") long id) {
        authorService.deleteAuthor(id);
        return "redirect:/authors";
    }

    /**
     * Метод для автодополнения авторов - возвращает HTML фрагмент
     */
    @GetMapping("/search-fragment")
    public String searchAuthorsFragment(@RequestParam("query") String query, Model model) {
        List<Author> foundAuthors = authorService.searchAuthorsByName(query);
        model.addAttribute("authors", foundAuthors);
        return "authors/search-fragment";
    }
}
