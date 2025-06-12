package com.example.bookshop.facade;

import com.example.bookshop.dto.PageResponse;
import com.example.bookshop.model.Author;
import com.example.bookshop.service.AuthorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AuthorFacade {

    private final AuthorService authorService;

    public AuthorFacade(AuthorService authorService) {
        this.authorService = authorService;
    }


    public List<Author> getAllAuthors() {
        return authorService.getAllAuthors();
    }


    public PageResponse<Author> getAllAuthors(int pageNumber, int pageSize) {
        return authorService.getAllAuthors(pageNumber, pageSize);
    }


    public Author getAuthorById(long id) {
        return authorService.getAuthorById(id);
    }

    public List<Author> searchAuthorsByName(String name) {
        return authorService.searchAuthorsByName(name);
    }


    public PageResponse<Author> searchAuthorsByName(String name, int pageNumber, int pageSize) {
        return authorService.searchAuthorsByName(name, pageNumber, pageSize);
    }


    public List<Author> getAuthorsByBookId(long bookId) {
        return authorService.getAuthorsByBookId(bookId);
    }


    public Author createAuthor(Author author) {
        return authorService.createAuthor(author);
    }

    public Author updateAuthor(long id, Author authorDetails) {
        return authorService.updateAuthor(id, authorDetails);
    }


    public void deleteAuthor(long id) {
        authorService.deleteAuthor(id);
    }


    public void addBookToAuthor(long authorId, long bookId) {
        authorService.addBookToAuthor(authorId, bookId);
    }


    public void removeBookFromAuthor(long authorId, long bookId) {
        authorService.removeBookFromAuthor(authorId, bookId);
    }
}