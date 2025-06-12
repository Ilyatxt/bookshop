package com.example.bookshop.dao;

import com.example.bookshop.model.Author;

import java.util.List;
import java.util.Optional;

/**
 * DAO интерфейс для работы с авторами
 */
public interface AuthorDao {

    /**
     * Получить всех авторов
     * 
     * @return список всех авторов
     */
    List<Author> findAll();

    /**
     * Получить всех авторов с пагинацией
     * 
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список авторов для указанной страницы
     */
    List<Author> findAll(int pageNumber, int pageSize);

    /**
     * Получить общее количество авторов
     * 
     * @return общее количество авторов
     */
    long countAll();

    /**
     * Получить автора по ID
     * 
     * @param id идентификатор автора
     * @return автор или пустое значение, если автор не найден
     */
    Optional<Author> findById(long id);

    /**
     * Найти авторов по имени и фамилии (частичное совпадение)
     * 
     * @param name часть имени или фамилии автора
     * @return список авторов, соответствующих поисковому запросу
     */
    List<Author> findByName(String name);

    /**
     * Найти авторов по имени и фамилии с пагинацией (частичное совпадение)
     * 
     * @param name часть имени или фамилии автора
     * @param pageNumber номер страницы (начиная с 0)
     * @param pageSize размер страницы
     * @return список авторов, соответствующих поисковому запросу для указанной страницы
     */
    List<Author> findByName(String name, int pageNumber, int pageSize);

    /**
     * Получить общее количество авторов, соответствующих поисковому запросу
     * 
     * @param name часть имени или фамилии автора
     * @return общее количество авторов, соответствующих запросу
     */
    long countByName(String name);

    /**
     * Получить авторов для указанной книги
     *
     * @param bookId идентификатор книги
     * @return список авторов книги
     */
    List<Author> findByBookId(long bookId);

    /**
     * Сохранить нового автора
     * 
     * @param author автор для сохранения
     * @return сохраненный автор с установленным ID
     */
    Author save(Author author);

    /**
     * Обновить существующего автора
     * 
     * @param author автор для обновления
     * @return обновленный автор
     */
    Author update(Author author);

    /**
     * Удалить автора по ID
     * 
     * @param id идентификатор автора для удаления
     * @return true если автор был удален, иначе false
     */
    boolean deleteById(long id);

    /**
     * Связать автора с книгой
     *
     * @param authorId идентификатор автора
     * @param bookId идентификатор книги
     * @return true если связь успешно установлена
     */
    boolean addBookToAuthor(long authorId, long bookId);

    /**
     * Удалить связь автора с книгой
     *
     * @param authorId идентификатор автора
     * @param bookId идентификатор книги
     * @return true если связь успешно удалена
     */
    boolean removeBookFromAuthor(long authorId, long bookId);
    
    
    
}
