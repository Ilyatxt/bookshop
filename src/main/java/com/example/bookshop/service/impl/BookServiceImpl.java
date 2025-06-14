package com.example.bookshop.service.impl;

import com.example.bookshop.dao.BookDao;
import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.exception.ResourceNotFoundException;
import com.example.bookshop.model.Author;
import com.example.bookshop.model.Book;
import com.example.bookshop.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с книгами
 */
@Service
public class BookServiceImpl implements BookService {

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookDao bookDao;

    public BookServiceImpl(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public boolean addGenreToBook(long bookId, long genreId) {
        return bookDao.addGenreToBook(bookId, genreId);
    }

    @Override
    public boolean removeGenreFromBook(long bookId, long genreId) {
        return bookDao.removeGenreFromBook(bookId, genreId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        log.debug("Получение списка всех книг");
        List<Book> books = bookDao.findAll();
        log.debug("Найдено {} книг", books.size());
        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Book> getAllBooks(int pageNumber, int pageSize) {
        log.debug("Получение списка книг с пагинацией: страница={}, размер={}", pageNumber, pageSize);
        List<Book> books = bookDao.findAll(pageNumber, pageSize);
        long totalBooks = bookDao.countAll();
        log.debug("Найдено {} книг из {} на странице {}", books.size(), totalBooks, pageNumber);
        return new PageResponse<>(books, pageNumber, pageSize, totalBooks);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(long id) {
        log.debug("Поиск книги по id: {}", id);
        Book book = bookDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Книга с id {} не найдена", id);
                    return new ResourceNotFoundException("Книга", "id", id);
                });
        log.debug("Найдена книга: {}", book);
        return book;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> searchBooksByTitle(String title) {
        log.debug("Поиск книг по названию: {}", title);
        List<Book> books = bookDao.findByTitle(title);
        log.debug("Найдено {} книг по названию {}", books.size(), title);
        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Book> searchBooksByTitle(String title, int pageNumber, int pageSize) {
        log.debug("Поиск книг по названию: {} с пагинацией: страница={}, размер={}", title, pageNumber, pageSize);
        List<Book> books = bookDao.findByTitle(title, pageNumber, pageSize);
        long totalBooks = bookDao.countByTitle(title);
        log.debug("Найдено {} книг из {} по названию {} на странице {}", books.size(), totalBooks, title, pageNumber);
        return new PageResponse<>(books, pageNumber, pageSize, totalBooks);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookByIsbn(String isbn) {
        log.debug("Поиск книги по ISBN: {}", isbn);
        Book book = bookDao.findByIsbn(isbn)
                .orElseThrow(() -> {
                    log.error("Книга с ISBN {} не найдена", isbn);
                    return new ResourceNotFoundException("Книга", "ISBN", isbn);
                });
        log.debug("Найдена книга по ISBN {}: {}", isbn, book);
        return book;
    }

    @Override
    @Transactional
    public Book createBook(Book book) {
        log.info("Создание новой книги: {}", book);

        if (bookDao.existsByTitleAndAllAuthors(book.getTitle(),
                getAuthorIdsFromAuthors(book.getAuthors()))) {
            throw new IllegalArgumentException("Книга с таким названием и автором уже существует.");
        }

        Book savedBook = bookDao.save(book);
        log.info("Книга успешно создана с id: {}", savedBook.getId());
        return savedBook;
    }

    @Override
    @Transactional
    public Book updateBook(Book book) {
        Book updatedBook = bookDao.update(book);
        log.info("Книга с id: {} успешно обновлена", book.getId());
        return updatedBook;
    }

    @Override
    @Transactional
    public void deleteBook(long id) {
        log.info("Удаление книги с id: {}", id);
        getBookById(id);

        boolean deleted = bookDao.deleteById(id);
        if (!deleted) {
            log.error("Не удалось удалить книгу с id: {}", id);
            throw new ResourceNotFoundException("Книга", "id", id);
        }
        log.info("Книга с id: {} успешно удалена", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getTopSellingBooks(int limit) {
        log.debug("Получение топ-{} самых продаваемых книг", limit);
        List<Book> books = bookDao.findTopSellingBooks(limit);
        log.debug("Найдено {} самых продаваемых книг", books.size());
        return books;
    }

    @Override
    @Transactional
    public void addAuthorToBook(long bookId, long authorId) {
        log.info("Добавление автора с id: {} к книге с id: {}", authorId, bookId);
        // Проверка существования книги
        getBookById(bookId);

        bookDao.addAuthorToBook(bookId, authorId);
        log.info("Автор с id: {} успешно добавлен к книге с id: {}", authorId, bookId);
    }

    @Override
    @Transactional
    public void removeAuthorFromBook(long bookId, long authorId) {
        log.info("Удаление автора с id: {} из книги с id: {}", authorId, bookId);
        // Проверка существования книги
        getBookById(bookId);

        bookDao.removeAuthorFromBook(bookId, authorId);
        log.info("Автор с id: {} успешно удален из книги с id: {}", authorId, bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthorId(long authorId) {
        log.debug("Получение списка книг автора с id: {}", authorId);
        List<Book> books = bookDao.findByAuthorId(authorId);
        log.debug("Найдено {} книг для автора с id: {}", books.size(), authorId);
        return books;
    }


    private List<Long> getAuthorIdsFromAuthors(List<Author> authors) {
        if (authors == null) {
            return new ArrayList<>(); // Или null, в зависимости от вашей логики
        }
        return authors.stream()
                .map(Author::getId) // Извлекаем ID из каждого объекта Author
                .filter(Objects::nonNull) // Опционально: отфильтровываем null-ID, если они возможны
                .collect(Collectors.toList()); // Собираем в новый список
    }
}
