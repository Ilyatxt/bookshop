package com.example.bookshop.service.impl;

import com.example.bookshop.dao.AuthorDao;
import com.example.bookshop.exception.ResourceNotFoundException;
import com.example.bookshop.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @Mock
    private AuthorDao authorDao;
    private AuthorServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuthorServiceImpl(authorDao);
    }

    @Test
    void getAuthorById_notFoundThrows() {
        when(authorDao.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getAuthorById(1L));
    }

    @Test
    void updateAuthor_updatesExistingAuthor() {
        Author existing = new Author();
        existing.setId(1L);
        when(authorDao.findById(1L)).thenReturn(Optional.of(existing));
        when(authorDao.update(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Author details = new Author();
        details.setFirstName("F");
        details.setLastName("L");
        details.setBio("B");

        Author result = service.updateAuthor(1L, details);

        ArgumentCaptor<Author> captor = ArgumentCaptor.forClass(Author.class);
        verify(authorDao).update(captor.capture());
        assertEquals("F", captor.getValue().getFirstName());
        assertSame(result, captor.getValue());
    }

    @Test
    void deleteAuthor_whenDaoReportsFailureThrows() {
        Author existing = new Author();
        existing.setId(2L);
        when(authorDao.findById(2L)).thenReturn(Optional.of(existing));
        when(authorDao.deleteById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteAuthor(2L));
    }
}
