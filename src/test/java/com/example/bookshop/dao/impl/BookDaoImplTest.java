package com.example.bookshop.dao.impl;

import com.example.bookshop.model.Book;
import com.example.bookshop.model.Currency;
import com.example.bookshop.model.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookDaoImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    private BookDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dao = new BookDaoImpl(jdbcTemplate);
    }

    @Test
    void findById_returnsBook() {
        Book book = new Book();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1L))).thenReturn(book);
        Optional<Book> result = dao.findById(1L);
        assertTrue(result.isPresent());
        assertSame(book, result.get());
    }

    @Test
    void findById_notFoundReturnsEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(2L)))
                .thenThrow(new EmptyResultDataAccessException(1));
        assertTrue(dao.findById(2L).isEmpty());
    }

    @Test
    void deleteById_returnsTrueWhenDeleted() {
        when(jdbcTemplate.update(anyString(), eq(3L))).thenReturn(1);
        assertTrue(dao.deleteById(3L));
    }
}
