package com.example.bookshop.model;

/**
 * Позиция заказа.
 */

import java.math.BigDecimal;
import java.util.Objects;

public class OrderEntry {
    private long id;

    private long orderId;

    private long bookId;

    private int quantity;

    private Double unitPrice;

    public OrderEntry() {
    }

    public OrderEntry(long id, long orderId, long bookId, int quantity, Double unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderEntry)) return false;
        OrderEntry that = (OrderEntry) o;
        return id == that.id && orderId == that.orderId && bookId == that.bookId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, bookId);
    }

    @Override
    public String toString() {
        return "OrderEntry{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", bookId=" + bookId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
