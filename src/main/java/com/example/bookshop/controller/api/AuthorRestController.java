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
 * REST контроллер для работы с авторами книг
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorRestController {

    private final DataSource dataSource;

    @Autowired
    public AuthorRestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Поиск авторов по части имени
     *
     * @param query часть имени автора
     * @return список найденных авторов в JSON формате
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchAuthors(@RequestParam("q") String query) {
        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, first_name, last_name FROM authors " +
                     "WHERE LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) " +
                     "ORDER BY last_name, first_name LIMIT 10")) {

            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> author = new HashMap<>();
                    author.put("id", rs.getLong("id"));
                    author.put("firstName", rs.getString("first_name"));
                    author.put("lastName", rs.getString("last_name"));
                    author.put("fullName", rs.getString("first_name") + " " + rs.getString("last_name"));
                    result.add(author);
                }
            }

        } catch (SQLException e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(result);
    }
}
