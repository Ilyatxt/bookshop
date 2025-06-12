package com.example.bookshop.service.impl;

import com.example.bookshop.dao.AuthorDao;
import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.exception.ResourceNotFoundException;
import com.example.bookshop.model.Author;
import com.example.bookshop.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация сервиса для работы с авторами
 */
@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorDao authorDao;

    public AuthorServiceImpl(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        log.debug("Получение списка всех авторов");
        List<Author> authors = authorDao.findAll();
        log.debug("Найдено {} авторов", authors.size());
        return authors;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Author> getAllAuthors(int pageNumber, int pageSize) {
        log.debug("Получение списка авторов с пагинацией: страница={}, размер={}", pageNumber, pageSize);
        List<Author> authors = authorDao.findAll(pageNumber, pageSize);
        long totalAuthors = authorDao.countAll();
        log.debug("Найдено {} авторов из {} на странице {}", authors.size(), totalAuthors, pageNumber);
        return new PageResponse<>(authors, pageNumber, pageSize, totalAuthors);
    }

    @Override
    @Transactional(readOnly = true)
    public Author getAuthorById(long id) {
        log.debug("Поиск автора по id: {}", id);
        Author author = authorDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Автор с id {} не найден", id);
                    return new ResourceNotFoundException("Автор", "id", id);
                });
        log.debug("Найден автор: {}", author);
        return author;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> searchAuthorsByName(String name) {
        log.debug("Поиск авторов по имени: {}", name);
        List<Author> authors = authorDao.findByName(name);
        log.debug("Найдено {} авторов по имени {}", authors.size(), name);
        return authors;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Author> searchAuthorsByName(String name, int pageNumber, int pageSize) {
        log.debug("Поиск авторов по имени: {} с пагинацией: страница={}, размер={}", name, pageNumber, pageSize);
        List<Author> authors = authorDao.findByName(name, pageNumber, pageSize);
        long totalAuthors = authorDao.countByName(name);
        log.debug("Найдено {} авторов из {} по имени {} на странице {}", authors.size(), totalAuthors, name, pageNumber);
        return new PageResponse<>(authors, pageNumber, pageSize, totalAuthors);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> getAuthorsByBookId(long bookId) {
        log.debug("Поиск авторов по id книги: {}", bookId);
        List<Author> authors = authorDao.findByBookId(bookId);
        log.debug("Найдено {} авторов для книги с id {}", authors.size(), bookId);
        return authors;
    }

    @Override
    @Transactional
    public Author createAuthor(Author author) {
        log.info("Создание нового автора: {}", author);
        Author savedAuthor = authorDao.save(author);
        log.info("Автор успешно создан с id: {}", savedAuthor.getId());
        return savedAuthor;
    }

    @Override
    @Transactional
    public Author updateAuthor(long id, Author authorDetails) {
        log.info("Обновление автора с id: {}", id);
        Author existingAuthor = getAuthorById(id);
        log.debug("Найден автор для обновления: {}", existingAuthor);

        existingAuthor.setFirstName(authorDetails.getFirstName());
        existingAuthor.setLastName(authorDetails.getLastName());
        existingAuthor.setBio(authorDetails.getBio());
        existingAuthor.setBirthdate(authorDetails.getBirthdate());
        existingAuthor.setCountry(authorDetails.getCountry());

        log.debug("Применение обновлений к автору: {}", existingAuthor);
        Author updatedAuthor = authorDao.update(existingAuthor);
        log.info("Автор с id: {} успешно обновлен", id);
        return updatedAuthor;
    }

    @Override
    @Transactional
    public void deleteAuthor(long id) {
        log.info("Удаление автора с id: {}", id);
        getAuthorById(id); // Проверка существования

        boolean deleted = authorDao.deleteById(id);
        if (!deleted) {
            log.error("Не удалось удалить автора с id: {}", id);
            throw new ResourceNotFoundException("Автор", "id", id);
        }
        log.info("Автор с id: {} успешно удален", id);
    }

    @Override
    @Transactional
    public void addBookToAuthor(long authorId, long bookId) {
        log.info("Добавление книги с id: {} к автору с id: {}", bookId, authorId);
        // Проверка существования автора
        getAuthorById(authorId);

        authorDao.addBookToAuthor(authorId, bookId);
        log.info("Книга с id: {} успешно добавлена к автору с id: {}", bookId, authorId);
    }

    @Override
    @Transactional
    public void removeBookFromAuthor(long authorId, long bookId) {
        log.info("Удаление книги с id: {} от автора с id: {}", bookId, authorId);
        // Проверка существования автора
        getAuthorById(authorId);

        authorDao.removeBookFromAuthor(authorId, bookId);
        log.info("Книга с id: {} успешно удалена от автора с id: {}", bookId, authorId);
    }
}
