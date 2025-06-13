package com.example.bookshop.dao.impl;

import com.example.bookshop.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    private OrderDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dao = new OrderDaoImpl(jdbcTemplate);
    }

    @Test
    void findById_notFoundReturnsEmpty() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(5L)))
                .thenThrow(new EmptyResultDataAccessException(1));
        assertTrue(dao.findById(5L).isEmpty());
    }

    @Test
    void deleteById_returnsFalseWhenNoRows() {
        when(jdbcTemplate.update(anyString(), eq(7L))).thenReturn(0);
        assertFalse(dao.deleteById(7L));
    }
}
