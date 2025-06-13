package com.example.bookshop.dao.impl;

import com.example.bookshop.model.Role;
import com.example.bookshop.model.User;
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

class UserDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    private UserDaoImpl dao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dao = new UserDaoImpl(jdbcTemplate);
    }

    @Test
    void findByEmail_returnsUser() {
        User user = new User();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("a@b.c"))).thenReturn(List.of(user));
        Optional<User> result = dao.findByEmail("a@b.c");
        assertTrue(result.isPresent());
        assertSame(user, result.get());
    }

    @Test
    void updateUserRole_executesUpdate() {
        dao.updateUserRole(1L, Role.ADMIN);
        verify(jdbcTemplate).update(anyString(), eq(Role.ADMIN.name()), eq(1L));
    }

    @Test
    void delete_callsJdbcTemplate() {
        dao.delete(3L);
        verify(jdbcTemplate).update(anyString(), eq(3L));
    }
}
