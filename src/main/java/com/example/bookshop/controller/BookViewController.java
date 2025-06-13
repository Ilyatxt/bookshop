package com.example.bookshop.controller;

import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.facade.BookFacade;
import com.example.bookshop.model.Author;
import com.example.bookshop.model.Book;
import com.example.bookshop.service.AuthorService;
import com.example.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * контроллер для отображения страниц с книгами
 */
@Controller
@RequestMapping("/books")
public class BookViewController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookFacade bookFacade;

    @Autowired
    public BookViewController(BookService bookService, AuthorService authorService, BookFacade bookFacade) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookFacade = bookFacade;
    }

    /**
     * Отображение списка всех книг
     */

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping
    public String getAllBooks(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        PageResponse<Book> bookPage = bookService.getAllBooks(page, size);
        model.addAttribute("bookPage", bookPage);
        return "books/list";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("user/books")
    public String getAllBoosForUser(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        PageResponse<Book> bookPage = bookService.getAllBooks(page, size);
        model.addAttribute("bookPage", bookPage);
        return "books/listForUser";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/{id}")
    public String getBookDetails(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/details";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{id}")
    public String getBooksDetailsForUser(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/detailsForUser";
    }

    /**
     * Страница поиска книг
     */
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        PageResponse<Book> bookPage;
        if (title != null && !title.isEmpty()) {
            bookPage = bookService.searchBooksByTitle(title, page, size);
            model.addAttribute("searchTerm", title);
        } else {
            bookPage = bookService.getAllBooks(page, size);
        }
        model.addAttribute("bookPage", bookPage);
        return "books/search";
    }

    /**
     * Отображение формы для создания новой книги
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }

    /**
     * Обработка создания новой книги
     */
    @PostMapping
    public String createBook(@ModelAttribute Book book,
                             @RequestParam(value = "authorId", required = false) Long authorId) {

        Book savedBook = bookFacade.createBook(book);
        bookFacade.addAuthorToBook(savedBook.getId(), authorId);

        return "redirect:/books/" + savedBook.getId();
    }

    /**
     * Страница управления авторами книги
     */
    @GetMapping("/{id}/authors")
    public String manageBookAuthors(@PathVariable(name = "id") long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);

        // Получаем всех авторов и удаляем из списка тех, которые уже связаны с книгой
        List<Author> allAuthors = authorService.getAllAuthors();
        if (book.getAuthors() != null) {
            allAuthors.removeAll(book.getAuthors());
        }
        model.addAttribute("availableAuthors", allAuthors);

        return "books/author-management";
    }

    /**
     * Добавление автора к книге
     */
    @PostMapping("/{bookId}/authors/{authorId}/add")
    public String addAuthorToBook(@PathVariable(name = "bookId") long bookId, @PathVariable(name = "authorId") long authorId) {
        bookService.addAuthorToBook(bookId, authorId);
        return "redirect:/books/" + bookId + "/authors";
    }

    /**
     * Удаление автора у книги
     */
    @PostMapping("/{bookId}/authors/{authorId}/remove")
    public String removeAuthorFromBook(@PathVariable(name = "bookId") long bookId, @PathVariable(name = "authorId") long authorId) {
        bookService.removeAuthorFromBook(bookId, authorId);
        return "redirect:/books/" + bookId + "/authors";
    }

    /**
     * Отображение формы для редактирования книги
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/form";
    }

    /**
     * Страница популярных книг
     */
    @GetMapping("/top-selling")
    public String getTopSellingBooks(@RequestParam(defaultValue = "10") int limit, Model model) {
        List<Book> books = bookService.getTopSellingBooks(limit);
        model.addAttribute("books", books);
        return "books/top-selling";
    }

}
