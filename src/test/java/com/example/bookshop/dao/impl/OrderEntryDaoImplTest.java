package com.example.bookshop.dao.impl;

import com.example.bookshop.model.OrderEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderEntryDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    private OrderEntryDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dao = new OrderEntryDaoImpl(jdbcTemplate);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(1L)))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));
        assertTrue(dao.findById(1L).isEmpty());
    }

    @Test
    void deleteById_returnsTrueWhenRowsDeleted() {
        when(jdbcTemplate.update(anyString(), eq(2L))).thenReturn(1);
        assertTrue(dao.deleteById(2L));
    }
}
