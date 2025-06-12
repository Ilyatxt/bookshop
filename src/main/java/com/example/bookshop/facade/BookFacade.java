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




}
