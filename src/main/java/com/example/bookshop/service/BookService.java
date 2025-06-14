package com.example.bookshop.service;


import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с книгами
 */

public interface BookService {

    /**
     * Получить все книги
     *
     * @return список всех книг
     */
    List<Book> getAllBooks();

    /**
     * Получить все книги с пагинацией
     *
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return объект PageResponse, содержащий данные пагинации и список книг
     */
    PageResponse<Book> getAllBooks(int pageNumber, int pageSize);

    /**
     * Получить книгу по ID
     *
     * @param id идентификатор книги
     * @return книга
     * @throws com.example.bookshop.exception.ResourceNotFoundException если книга не найдена
     */
    Book getBookById(long id);

    /**
     * Найти книги по заголовку (частичное совпадение)
     *
     * @param title часть заголовка книги
     * @return список книг, соответствующих поисковому запросу
     */
    List<Book> searchBooksByTitle(String title);

    /**
     * Найти книги по заголовку с пагинацией (частичное совпадение)
     *
     * @param title часть заголовка книги
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return объект PageResponse, содержащий данные пагинации и список книг, соответствующих запросу
     */
    PageResponse<Book> searchBooksByTitle(String title, int pageNumber, int pageSize);

    /**
     * Найти книгу по ISBN
     *
     * @param isbn ISBN книги
     * @return книга
     * @throws com.example.bookshop.exception.ResourceNotFoundException если книга не найдена
     */
    Book getBookByIsbn(String isbn);

    /**
     * Создать новую книгу
     *
     * @param book данные новой книги
     * @return созданная книга с установленным ID
     */
    Book createBook(Book book);

    /**
     * Обновить существующую книгу
     *
     * @param id идентификатор книги для обновления
     * @param bookDetails новые данные книги
     * @return обновленная книга
     * @throws com.example.bookshop.exception.ResourceNotFoundException если книга не найдена
     */
    Book updateBook(Book bookDetails);

    /**
     * Удалить книгу по ID
     *
     * @param id идентификатор книги для удаления
     * @throws com.example.bookshop.exception.ResourceNotFoundException если книга не найдена
     */
    void deleteBook(long id);

    /**
     * Получить список популярных книг, отсортированных по количеству продаж
     *
     * @param limit максимальное количество книг для возврата
     * @return список популярных книг
     */
    List<Book> getTopSellingBooks(int limit);

    /**
     * Связать книгу с автором
     *
     * @param bookId идентификатор книги
     * @param authorId идентификатор автора
     */
    void addAuthorToBook(long bookId, long authorId);

    /**
     * Удалить связь книги с автором
     *
     * @param bookId идентификатор книги
     * @param authorId идентификатор автора
     */
    void removeAuthorFromBook(long bookId, long authorId);

    /**
     * Получить все книги автора
     *
     * @param authorId идентификатор автора
     * @return список книг автора
     */
    List<Book> getBooksByAuthorId(long authorId);

    boolean addGenreToBook(long bookId, long genreId);

    boolean removeGenreFromBook(long bookId, long genreId);

}
