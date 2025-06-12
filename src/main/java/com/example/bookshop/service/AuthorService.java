package com.example.bookshop.service;

import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.model.Author;

import java.util.List;

/**
 * Сервис для работы с авторами
 */
public interface AuthorService {

    /**
     * Получить всех авторов
     * 
     * @return список всех авторов
     */
    List<Author> getAllAuthors();

    /**
     * Получить всех авторов с пагинацией
     * 
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return объект PageResponse, содержащий данные пагинации и список авторов
     */
    PageResponse<Author> getAllAuthors(int pageNumber, int pageSize);

    /**
     * Получить автора по ID
     * 
     * @param id идентификатор автора
     * @return автор
     * @throws com.example.bookshop.exception.ResourceNotFoundException если автор не найден
     */
    Author getAuthorById(long id);

    /**
     * Найти авторов по имени и фамилии (частичное совпадение)
     * 
     * @param name часть имени или фамилии автора
     * @return список авторов, соответствующих поисковому запросу
     */
    List<Author> searchAuthorsByName(String name);

    /**
     * Найти авторов по имени и фамилии с пагинацией (частичное совпадение)
     * 
     * @param name часть имени или фамилии автора
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return объект PageResponse, содержащий данные пагинации и список авторов, соответствующих запросу
     */
    PageResponse<Author> searchAuthorsByName(String name, int pageNumber, int pageSize);

    /**
     * Получить авторов для указанной книги
     *
     * @param bookId идентификатор книги
     * @return список авторов книги
     */
    List<Author> getAuthorsByBookId(long bookId);

    /**
     * Создать нового автора
     * 
     * @param author данные нового автора
     * @return созданный автор с установленным ID
     */
    Author createAuthor(Author author);

    /**
     * Обновить существующего автора
     * 
     * @param id идентификатор автора для обновления
     * @param authorDetails новые данные автора
     * @return обновленный автор
     * @throws com.example.bookshop.exception.ResourceNotFoundException если автор не найден
     */
    Author updateAuthor(long id, Author authorDetails);

    /**
     * Удалить автора по ID
     * 
     * @param id идентификатор автора для удаления
     * @throws com.example.bookshop.exception.ResourceNotFoundException если автор не найден
     */
    void deleteAuthor(long id);

    /**
     * Связать автора с книгой
     *
     * @param authorId идентификатор автора
     * @param bookId идентификатор книги
     */
    void addBookToAuthor(long authorId, long bookId);

    /**
     * Удалить связь автора с книгой
     *
     * @param authorId идентификатор автора
     * @param bookId идентификатор книги
     */
    void removeBookFromAuthor(long authorId, long bookId);
}
