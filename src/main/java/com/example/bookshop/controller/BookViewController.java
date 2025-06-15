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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * контроллер для отображения страниц с книгами
 */
@Controller
@RequestMapping("/books")
public class BookViewController {

    private static final Logger log = LoggerFactory.getLogger(BookViewController.class);

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
    public String getAllBooks(@RequestParam(defaultValue = "0", name = "page") int page,
                              @RequestParam(defaultValue = "10", name = "size") int size,
                              Model model) {
        log.debug("Запрос всех книг page={}, size={}", page, size);
        PageResponse<Book> bookPage = bookService.getAllBooks(page, size);
        model.addAttribute("page", bookPage);
        return "books/list";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("user/books")
    public String getAllBoosForUser(@RequestParam(defaultValue = "0",name = "page") int page,
                                    @RequestParam(defaultValue = "10", name = "size") int size,
                                    Model model) {
        log.debug("Запрос всех книг для пользователя page={}, size={}", page, size);
        PageResponse<Book> bookPage = bookService.getAllBooks(page, size);
        model.addAttribute("bookPage", bookPage);
        return "books/listForUser";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/{id}")
    public String getBookDetails(@PathVariable(name = "id") long id, Model model) {
        log.debug("Детали книги {}", id);
        model.addAttribute("book", bookService.getBookById(id));
        return "books/details";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{id}")
    public String getBooksDetailsForUser(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "books/detailsForUser";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/genre/{name}")
    public String getBooksByGenre(@PathVariable("name") String genre,
                                  @RequestParam(defaultValue = "0", name = "page") int page,
                                  @RequestParam(defaultValue = "10", name = "size") int size,
                                  Model model) {
        PageResponse<Book> bookPage = bookService.getBooksByGenre(genre, page, size);
        model.addAttribute("page", bookPage);
        model.addAttribute("selectedGenre", genre);
        return "books/list";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/genre/{name}")
    public String getBooksByGenreForUser(@PathVariable("name") String genre,
                                         @RequestParam(defaultValue = "0", name = "page") int page,
                                         @RequestParam(defaultValue = "10", name = "size") int size,
                                         Model model) {
        PageResponse<Book> bookPage = bookService.getBooksByGenre(genre, page, size);
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("selectedGenre", genre);
        return "books/listForUser";
    }

    /**
     * Страница поиска книг
     */
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
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

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/search")
    public String searchBooksForUser(
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            Model model) {
        PageResponse<Book> bookPage;
        if (title != null && !title.isEmpty()) {
            bookPage = bookService.searchBooksByTitle(title, page, size);
            model.addAttribute("searchTerm", title);
        } else {
            bookPage = bookService.getAllBooks(page, size);
        }
        model.addAttribute("bookPage", bookPage);
        return "books/searchForUser";
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
                             @RequestParam(value = "authorIds", required = false) String authorIds,
                             @RequestParam(value = "genreIds", required = false) String genreIds) {
        Book savedBook = bookFacade.createBookWithRelations(book, authorIds, genreIds);
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
    public String addAuthorToBook(@PathVariable(name = "bookId") long bookId,
                                  @PathVariable(name = "authorId") long authorId) {
        bookFacade.addAuthorToBook(bookId, authorId);
        return "redirect:/books/" + bookId + "/authors";
    }

    /**
     * Удаление автора у книги
     */
    @PostMapping("/{bookId}/authors/{authorId}/remove")
    public String removeAuthorFromBook(@PathVariable(name = "bookId") long bookId,
                                       @PathVariable(name = "authorId") long authorId) {
        bookFacade.removeAuthorFromBook(bookId, authorId);
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
    public String getTopSellingBooks(@RequestParam(defaultValue = "10", name = "limit") int limit, Model model) {
        List<Book> books = bookService.getTopSellingBooks(limit);
        model.addAttribute("books", books);
        return "books/top-selling";
    }

}
