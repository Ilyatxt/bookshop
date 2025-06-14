package com.example.bookshop.controller.api;

import com.example.bookshop.dao.AuthorDao;
import com.example.bookshop.model.Author;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private final AuthorDao authorDao;

    public AuthorRestController(AuthorDao authorDao) {
        this.authorDao = authorDao;
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

        List<Author> authors = authorDao.findByName(query, 0, 10);
        for (Author author : authors) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", author.getId());
            map.put("firstName", author.getFirstName());
            map.put("lastName", author.getLastName());
            map.put("fullName", author.getFirstName() + " " + author.getLastName());
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }
}
