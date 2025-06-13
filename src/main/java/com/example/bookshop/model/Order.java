package com.example.bookshop.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import com.example.bookshop.model.OrderStatus;

/**
 * Модель заказа
 */


public class Order {

    private long id;

    private long userId;

    private OffsetDateTime orderDate;

    private String orderCode;

    private BigDecimal totalPrice;

    private Currency currency = Currency.USD;

    private List<OrderEntry> entries;

    private OrderStatus status = OrderStatus.NEW;

    public Order() {
    }

    public Order(long id, long userId, OffsetDateTime orderDate, String orderCode,
                 BigDecimal totalPrice, Currency currency, List<OrderEntry> entries,
                 OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.orderCode = orderCode;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.entries = entries;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public OffsetDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(OffsetDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<OrderEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<OrderEntry> entries) {
        this.entries = entries;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id == order.id && orderCode.equals(order.orderCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderCode);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", orderCode='" + orderCode + '\'' +
                ", totalPrice=" + totalPrice +
                ", currency=" + currency +
                ", entries=" + entries +
                ", status=" + status +
                '}';
    }
}
