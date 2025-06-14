package com.example.bookshop.dao;

import com.example.bookshop.model.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAO интерфейс для работы с книгами
 */
public interface BookDao {

    /**
     * Получить все книги
     *
     * @return список всех книг
     */
    List<Book> findAll();

    /**
     * Получить все книги с пагинацией
     *
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список книг для указанной страницы
     */
    List<Book> findAll(int pageNumber, int pageSize);

    /**
     * Получить общее количество книг
     *
     * @return общее количество книг
     */
    long countAll();

    /**
     * Получить книгу по ID
     *
     * @param id идентификатор книги
     * @return книга или пустое значение, если книга не найдена
     */
    Optional<Book> findById(long id);

    /**
     * Найти книги по заголовку (частичное совпадение)
     *
     * @param title часть заголовка книги
     * @return список книг, соответствующих поисковому запросу
     */
    List<Book> findByTitle(String title);

    /**
     * Найти книги по заголовку с пагинацией (частичное совпадение)
     *
     * @param title часть заголовка книги
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список книг, соответствующих поисковому запросу для указанной страницы
     */
    List<Book> findByTitle(String title, int pageNumber, int pageSize);

    /**
     * Получить общее количество книг, соответствующих поисковому запросу
     *
     * @param title часть заголовка книги
     * @return общее количество книг, соответствующих запросу
     */
    long countByTitle(String title);

    /**
     * Найти книгу по ISBN
     *
     * @param isbn ISBN книги
     * @return книга или пустое значение, если книга не найдена
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Сохранить новую книгу
     *
     * @param book книга для сохранения
     * @return сохраненная книга с установленным ID
     */
    Book save(Book book);

    /**
     * Обновить существующую книгу
     *
     * @param book книга для обновления
     * @return обновленная книга
     */
    Book update(Book book);

    /**
     * Удалить книгу по ID
     *
     * @param id идентификатор книги для удаления
     * @return true если книга была удалена, иначе false
     */
    boolean deleteById(long id);

    /**
     * Получить список популярных книг, отсортированных по количеству продаж
     *
     * @param limit максимальное количество книг для возврата
     * @return список популярных книг
     */
    List<Book> findTopSellingBooks(int limit);
    
    

    /**
     * Связать книгу с автором
     *
     * @param bookId идентификатор книги
     * @param authorId идентификатор автора
     * @return true если связь успешно установлена
     */
    boolean addAuthorToBook(long bookId, long authorId);

    /**
     * Удалить связь книги с автором
     *
     * @param bookId идентификатор книги
     * @param authorId идентификатор автора
     * @return true если связь успешно удалена
     */
    boolean removeAuthorFromBook(long bookId, long authorId);

    /**
     * Связать книгу с жанром
     *
     * @param bookId идентификатор книги
     * @param genreId идентификатор жанра
     * @return true если связь успешно установлена
     */
    boolean addGenreToBook(long bookId, long genreId);

    /**
     * Удалить связь книги с жанром
     *
     * @param bookId идентификатор книги
     * @param genreId идентификатор жанра
     * @return true если связь успешно удалена
     */
    boolean removeGenreFromBook(long bookId, long genreId);

    /**
     * Найти все книги автора
     *
     * @param authorId идентификатор автора
     * @return список книг автора
     */
    List<Book> findByAuthorId(long authorId);

    /**
     * Проверяет, существует ли книга с указанным названием и автором
     *
     * @param title название книги
     * @param authorId идентификатор автора
     * @return true, если книга существует, иначе false
     */
    boolean existsByTitleAndAuthorId(String title, long authorId);

    boolean existsByTitleAndAllAuthors(String title, List<Long> authorIds);

    /**
     * Поиск жанров по части названия
     * @param query часть названия жанра
     * @return список жанров (id и name)
     */
    List<Map<String, Object>> searchGenres(String query);
}
