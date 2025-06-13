package com.example.bookshop.service.impl;

import com.example.bookshop.dao.UserDao;
import com.example.bookshop.model.Role;
import com.example.bookshop.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserServiceImpl(userDao, passwordEncoder);
    }

    @Test
    void register_encodesPasswordAndSavesUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("plain");

        when(userDao.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = service.register(user);

        assertEquals("encoded", saved.getPasswordHash());
        assertNotNull(saved.getCreatedAt());
        verify(userDao).save(saved);
    }

    @Test
    void register_existingEmailThrowsException() {
        when(userDao.findByEmail("dup@example.com")).thenReturn(Optional.of(new User()));

        User user = new User();
        user.setEmail("dup@example.com");

        assertThrows(IllegalArgumentException.class, () -> service.register(user));
    }

    @Test
    void updateProfile_usesExistingPassword() {
        User existing = new User();
        existing.setId(1L);
        existing.setPasswordHash("hash");

        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User update = new User();
        update.setId(1L);
        update.setEmail("new@example.com");
        update.setPasswordHash("ignored");

        service.updateProfile(update);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).save(captor.capture());
        assertEquals("hash", captor.getValue().getPasswordHash());
    }

    @Test
    void changePassword_encodesAndSaves() {
        User existing = new User();
        existing.setId(2L);
        existing.setPasswordHash("old");
        when(userDao.findById(2L)).thenReturn(Optional.of(existing));
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("newPass")).thenReturn("newHash");

        service.changePassword(existing, "newPass");

        verify(userDao).save(existing);
        assertEquals("newHash", existing.getPasswordHash());
    }
}
