package com.example.bookshop.dao.impl;

import com.example.bookshop.dao.OrderEntryDao;
import com.example.bookshop.model.OrderEntry;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Реализация DAO для работы с позициями заказа через JDBC
 */
@Repository
public class OrderEntryDaoImpl implements OrderEntryDao {

    private static final Logger log = LoggerFactory.getLogger(OrderEntryDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<OrderEntry> orderEntryRowMapper = (rs, rowNum) -> {
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setId(rs.getLong("id"));
        orderEntry.setOrderId(rs.getLong("order_id"));
        orderEntry.setBookId(rs.getLong("book_id"));
        orderEntry.setQuantity(rs.getInt("quantity"));
        orderEntry.setUnitPrice(rs.getDouble("unit_price"));
        return orderEntry;
    };

    public OrderEntryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<OrderEntry> findByOrderId(long orderId) {
        String sql = "SELECT * FROM order_entry WHERE order_id = ?";
        return jdbcTemplate.query(sql, orderEntryRowMapper, orderId);
    }

    @Override
    public Optional<OrderEntry> findById(long id) {
        try {
            String sql = "SELECT * FROM order_entry WHERE id = ?";
            OrderEntry orderEntry = jdbcTemplate.queryForObject(sql, orderEntryRowMapper, id);
            return Optional.ofNullable(orderEntry);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Позиция заказа с id {} не найдена", id);
            return Optional.empty();
        }
    }

    @Override
    public OrderEntry save(OrderEntry orderEntry) {
        String sql = "INSERT INTO order_entry (order_id, book_id, quantity, unit_price) "
                + "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, orderEntry.getOrderId());
            ps.setLong(2, orderEntry.getBookId());
            ps.setInt(3, orderEntry.getQuantity());
            ps.setDouble(4, orderEntry.getUnitPrice());
            return ps;
        }, keyHolder);

        orderEntry.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Создана новая позиция заказа с id: {}", orderEntry.getId());

        return orderEntry;
    }

    @Override
    public List<OrderEntry> saveAll(List<OrderEntry> orderEntries) {
        List<OrderEntry> savedEntries = new ArrayList<>();
        for (OrderEntry entry : orderEntries) {
            savedEntries.add(save(entry));
        }
        return savedEntries;
    }

    @Override
    public OrderEntry update(OrderEntry orderEntry) {
        String sql = "UPDATE order_entry SET order_id = ?, book_id = ?, quantity = ?, "
                + "unit_price = ? WHERE id = ?";

        int updatedRows = jdbcTemplate.update(sql,
                orderEntry.getOrderId(),
                orderEntry.getBookId(),
                orderEntry.getQuantity(),
                orderEntry.getUnitPrice(),
                orderEntry.getId());

        if (updatedRows == 0) {
            throw new RuntimeException("Не удалось обновить позицию заказа с ID: " + orderEntry.getId());
        }

        log.info("Позиция заказа с id {} успешно обновлена", orderEntry.getId());
        return orderEntry;
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM order_entry WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);

        if (deletedRows > 0) {
            log.info("Позиция заказа с id {} успешно удалена", id);
            return true;
        } else {
            log.warn("Позиция заказа с id {} не найдена для удаления", id);
            return false;
        }
    }

    @Override
    public int deleteByOrderId(long orderId) {
        String sql = "DELETE FROM order_entry WHERE order_id = ?";
        int deletedRows = jdbcTemplate.update(sql, orderId);

        log.info("Удалено {} позиций для заказа с id {}", deletedRows, orderId);
        return deletedRows;
    }
}