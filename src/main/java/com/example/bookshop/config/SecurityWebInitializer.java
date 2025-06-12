package com.example.bookshop.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Инициализатор для регистрации фильтров Spring Security
 * Автоматически добавляет DelegatingFilterProxy в цепочку фильтров
 */
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {
    // Пустой класс, т.к. наследование от AbstractSecurityWebApplicationInitializer
    // уже выполняет всю необходимую работу по инициализации
}
