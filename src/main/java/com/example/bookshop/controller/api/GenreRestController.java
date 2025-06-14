package com.example.bookshop.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST контроллер для работы с жанрами книг
 */
@RestController
@RequestMapping("/api/genres")
public class GenreRestController {

    private final DataSource dataSource;

    @Autowired
    public GenreRestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Поиск жанров по части названия
     *
     * @param query часть названия жанра
     * @return список найденных жанров
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchGenres(@RequestParam("q") String query) {
        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, name FROM genres WHERE LOWER(name) LIKE LOWER(?) ORDER BY name LIMIT 10")) {

            stmt.setString(1, "%" + query + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> genre = new HashMap<>();
                    genre.put("id", rs.getLong("id"));
                    genre.put("name", rs.getString("name"));
                    result.add(genre);
                }
            }

        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(result);
    }
}
