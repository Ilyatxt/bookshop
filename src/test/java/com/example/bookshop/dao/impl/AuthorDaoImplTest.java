package com.example.bookshop.dao.impl;

import com.example.bookshop.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthorDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    private AuthorDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dao = new AuthorDaoImpl(jdbcTemplate);
    }

    @Test
    void findById_returnsAuthor() {
        Author author = new Author();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(List.of(author));
        Optional<Author> result = dao.findById(1L);
        assertTrue(result.isPresent());
        assertSame(author, result.get());
    }

    @Test
    void deleteById_returnsFalseWhenZeroUpdated() {
        when(jdbcTemplate.update(anyString(), eq(5L))).thenReturn(0);
        assertFalse(dao.deleteById(5L));
    }

    @Test
    void findAll_withPagination() {
        List<Author> authors = List.of(new Author());
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10), eq(20)))
                .thenReturn(authors);
        List<Author> result = dao.findAll(2, 10);
        assertSame(authors, result);
    }

    @Test
    void countAll_returnsValue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(7L);
        assertEquals(7L, dao.countAll());
    }

    @Test
    void countByName_returnsValue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("%john%"), eq("%john%")))
                .thenReturn(3L);
        assertEquals(3L, dao.countByName("john"));
    }
}
