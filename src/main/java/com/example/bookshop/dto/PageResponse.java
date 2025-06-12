package com.example.bookshop.dto;

import java.util.List;

/**
 * Класс для представления данных пагинации
 * @param <T> тип данных в коллекции
 */
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
    private boolean isFirst;

    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (pageSize > 0 && totalElements > 0) ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        this.isFirst = pageNumber == 0;
        this.isLast = (pageNumber + 1) >= totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return isLast;
    }

    public boolean isFirst() {
        return isFirst;
    }
}
