package com.example.bookshop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Основная конфигурация приложения.
 * Импортирует другие конфигурационные классы и определяет
 * пакеты для сканирования компонентов.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.example.bookshop.service",
        "com.example.bookshop.dao",
        "com.example.bookshop.controller"
})
@Import({
        DatabaseConfig.class,
        FlywayConfig.class,
        SecurityConfig.class,
})
public class AppConfig {

}
