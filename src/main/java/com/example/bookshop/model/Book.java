package com.example.bookshop.model;

/**
 * Модель книги.
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

//добавить автора и жанр  - составной индекс
public class Book {
    private long id;
    private String title;
    private String description;
    private Language language;
    private String isbn;
    private LocalDate publishedAt;
    private String coverImageUrl;
    private BigDecimal price;
    private Currency currency = Currency.USD;
    private OffsetDateTime createdAt;
    private int soldCount;
    private List<String> genres;
    private List<Author> authors;

    public Book() {
    }

    public Book(long id, String title, String description, Language language, String isbn,
                LocalDate publishedAt, String coverImageUrl, BigDecimal price, Currency currency,
                OffsetDateTime createdAt, int soldCount, List<String> genres, List<Author> authors) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.language = language;
        this.isbn = isbn;
        this.publishedAt = publishedAt;
        this.coverImageUrl = coverImageUrl;
        this.price = price;
        this.currency = currency;
        this.createdAt = createdAt;
        this.soldCount = soldCount;
        this.genres = genres;
        this.authors = authors;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDate publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id == book.id && Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", language=" + language +
                ", isbn='" + isbn + '\'' +
                ", publishedAt=" + publishedAt +
                ", price=" + price +
                ", currency=" + currency +
                ", soldCount=" + soldCount +
                ", genres=" + genres +
                ", authors=" + authors +
                '}';
    }
}
