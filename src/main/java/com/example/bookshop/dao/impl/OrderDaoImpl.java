
package com.example.bookshop.dao.impl;

import com.example.bookshop.dao.OrderDao;
import com.example.bookshop.model.Currency;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Реализация DAO для работы с заказами через JDBC
 */
@Repository
public class OrderDaoImpl implements OrderDao {

    private static final Logger log = LoggerFactory.getLogger(OrderDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setOrderDate(rs.getObject("order_date", OffsetDateTime.class));
        order.setOrderCode(rs.getString("order_code"));
        order.setTotalPrice(rs.getBigDecimal("total_price"));
        order.setCurrency(Currency.valueOf(rs.getString("currency")));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        // Позиции заказа загружаются отдельно через OrderEntryDao
        return order;
    };

    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, orderRowMapper);
    }

    @Override
    public List<Order> findAll(int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        String sql = "SELECT * FROM orders ORDER BY order_date DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, orderRowMapper, pageSize, offset);
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM orders";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public Optional<Order> findById(long id) {
        try {
            String sql = "SELECT * FROM orders WHERE id = ?";
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper, id);
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Заказ с id {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findByUserId(long userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, orderRowMapper, userId);
    }

    @Override
    public List<Order> findByUserIdAndStatusIn(long userId, List<OrderStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        String inSql = String.join(",", java.util.Collections.nCopies(statuses.size(), "?"));
        String sql = "SELECT * FROM orders WHERE user_id = ? AND status IN (" + inSql + ") ORDER BY order_date DESC";
        Object[] args = new Object[statuses.size() + 1];
        args[0] = userId;
        for (int i = 0; i < statuses.size(); i++) {
            args[i + 1] = statuses.get(i).name();
        }
        return jdbcTemplate.query(sql, args, orderRowMapper);
    }

    @Override
    public Optional<Order> findByOrderCode(String orderCode) {
        try {
            String sql = "SELECT * FROM orders WHERE order_code = ?";
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper, orderCode);
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Заказ с кодом {} не найден", orderCode);
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        String sql = "SELECT * FROM orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, orderRowMapper,
                Timestamp.from(startDate.toInstant()),
                Timestamp.from(endDate.toInstant()));
    }

    @Override
    public Order save(Order order) {
        String sql = "INSERT INTO orders (user_id, order_date, order_code, total_price, currency, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, order.getUserId());
            ps.setObject(2, Timestamp.from(order.getOrderDate().toInstant()));
            ps.setString(3, order.getOrderCode());
            ps.setBigDecimal(4, order.getTotalPrice());
            ps.setString(5, order.getCurrency().name());
            ps.setString(6, order.getStatus().name());
            return ps;
        }, keyHolder);

        order.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Создан новый заказ с id: {}", order.getId());

        return order;
    }

    @Override
    public Order update(Order order) {
        String sql = "UPDATE orders SET user_id = ?, order_date = ?, order_code = ?, "
                + "total_price = ?, currency = ?, status = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                order.getUserId(),
                Timestamp.from(order.getOrderDate().toInstant()),
                order.getOrderCode(),
                order.getTotalPrice(),
                order.getCurrency().name(),
                order.getStatus().name(),
                order.getId());

        if (updatedRows == 0) {
            throw new RuntimeException("Не удалось обновить заказ с ID: " + order.getId());
        }

        log.info("Заказ с id {} успешно обновлен", order.getId());
        return order;
    }

    @Override
    public int updateStatus(long id, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.name(), id);
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);

        if (deletedRows > 0) {
            log.info("Заказ с id {} успешно удален", id);
            return true;
        } else {
            log.warn("Заказ с id {} не найден для удаления", id);
            return false;
        }
    }
}