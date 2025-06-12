package com.example.bookshop.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое, когда запрашиваемый ресурс не найден
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением
     * 
     * @param message сообщение об ошибке
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с сообщением о ресурсе, который не был найден
     * 
     * @param resourceName название ресурса
     * @param fieldName название поля
     * @param fieldValue значение поля
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s не найден с %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
