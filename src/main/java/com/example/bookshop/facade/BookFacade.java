package com.example.bookshop.facade;

import com.example.bookshop.model.Book;
import com.example.bookshop.service.BookService;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class BookFacade {

    private final BookService bookService;

    public BookFacade(BookService bookService) {
        this.bookService = bookService;
    }

    public Book createBook(Book book) {
        book.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        book.setSoldCount(0);
        return bookService.createBook(book);
    }

    /**
     * Создание книги вместе с привязками авторов и жанров. Метод принимает
     * строки идентификаторов, разделенные запятыми, разбирает их и создает
     * необходимые связи.
     */
    public Book createBookWithRelations(Book book, String authorIds, String genreIds) {
        Book savedBook = createBook(book);

        if (authorIds != null && !authorIds.isEmpty()) {
            for (String id : authorIds.split(",")) {
                if (!id.isBlank()) {
                    try {
                        addAuthorToBook(savedBook.getId(), Long.parseLong(id));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        if (genreIds != null && !genreIds.isEmpty()) {
            for (String id : genreIds.split(",")) {
                if (!id.isBlank()) {
                    try {
                        addGenreToBook(savedBook.getId(), Long.parseLong(id));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        return savedBook;
    }

    public Book updateBook(Book book) {

        Book existingBook = bookService.getBookById(book.getId());

        existingBook.setTitle(book.getTitle());
        existingBook.setDescription(book.getDescription());
        existingBook.setLanguage(book.getLanguage());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setPublishedAt(book.getPublishedAt());
        existingBook.setCoverImageUrl(book.getCoverImageUrl());
        existingBook.setPrice(book.getPrice());
        existingBook.setCurrency(book.getCurrency());
        existingBook.setGenres(book.getGenres());
        existingBook.setAuthors(book.getAuthors());

        return bookService.updateBook(book);
    }

    public void addAuthorToBook(long bookId, long authorId) {
        bookService.addAuthorToBook(bookId, authorId);
    }
    public void removeAuthorFromBook(long bookId, long authorId) {
        bookService.removeAuthorFromBook(bookId, authorId);
    }
    public void addGenreToBook(long bookId, long genreId) {
        bookService.addGenreToBook(bookId, genreId);
    }
    public void removeGenreFromBook(long bookId, long genreId) {
        bookService.removeGenreFromBook(bookId, genreId);
    }

    public java.util.List<java.util.Map<String, Object>> searchGenres(String query) {
        return bookService.searchGenres(query);
    }



}

