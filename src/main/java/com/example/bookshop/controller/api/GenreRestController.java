package com.example.bookshop.controller.api;

import com.example.bookshop.facade.BookFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * REST контроллер для работы с жанрами книг
 */
@RestController
@RequestMapping("/api/genres")
public class GenreRestController {

    private static final Logger log = LoggerFactory.getLogger(GenreRestController.class);

    private final BookFacade bookFacade;

    public GenreRestController(BookFacade bookFacade) {
        this.bookFacade = bookFacade;
    }

    /**
     * Поиск жанров по части названия
     *
     * @param query часть названия жанра
     * @return список найденных жанров
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchGenres(@RequestParam("q") String query) {
        log.debug("REST поиск жанров по запросу {}", query);
        List<Map<String, Object>> result = bookFacade.searchGenres(query);
        return ResponseEntity.ok(result);
    }
}
