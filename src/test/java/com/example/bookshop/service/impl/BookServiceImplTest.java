package com.example.bookshop.service.impl;

import com.example.bookshop.dao.BookDao;
import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.exception.ResourceNotFoundException;
import com.example.bookshop.model.Author;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Language;
import com.example.bookshop.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookDao bookDao;
    private BookServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new BookServiceImpl(bookDao);
    }

    @Test
    void getBookById_notFoundThrows() {
        when(bookDao.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getBookById(1L));
    }

    @Test
    void createBook_whenDuplicateExistsThrows() {
        Book book = new Book();
        book.setTitle("T");
        book.setAuthors(List.of(new Author()));
        when(bookDao.existsByTitleAndAllAuthors(eq("T"), anyList())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.createBook(book));
    }

    @Test
    void deleteBook_whenDaoFailsThrows() {
        Book existing = new Book();
        existing.setId(2L);
        when(bookDao.findById(2L)).thenReturn(Optional.of(existing));
        when(bookDao.deleteById(2L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteBook(2L));
    }

    @Test
    void getAllBooks_returnsList() {
        when(bookDao.findAll()).thenReturn(Collections.emptyList());
        assertTrue(service.getAllBooks().isEmpty());
    }
}
