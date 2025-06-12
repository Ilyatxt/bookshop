package com.example.bookshop.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PreDestroy;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Конфигурация базы данных приложения с использованием пула соединений
 */
@Configuration
@PropertySource("classpath:application.properties")
public class DatabaseConfig {

    @Value("${db.url:jdbc:postgresql://localhost:5432/bookshop}")
    private String dbUrl;

    @Value("${db.username:postgres}")
    private String dbUsername;

    @Value("${db.password:2121}")
    private String dbPassword;

    @Value("${db.driver:org.postgresql.Driver}")
    private String dbDriver;

    @Value("${db.pool.initialSize:5}")
    private int initialSize;

    @Value("${db.pool.maxTotal:10}")
    private int maxTotal;

    @Value("${db.pool.maxIdle:8}")
    private int maxIdle;

    @Value("${db.pool.minIdle:2}")
    private int minIdle;

    @Value("${db.pool.maxWaitMillis:10000}")
    private long maxWaitMillis;

    private BasicDataSource dataSource;

    /**
     * Создает и настраивает источник данных с пулом соединений
     * 
     * @return настроенный источник данных с пулом соединений
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);

        // Настройка пула соединений
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxTotal(maxTotal);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxWaitMillis(maxWaitMillis);

        // Дополнительные настройки для повышения производительности
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(5);
        dataSource.setTimeBetweenEvictionRunsMillis(30000);

        this.dataSource = dataSource;
        return dataSource;
    }

    /**
     * Создает JdbcTemplate с использованием настроенного источника данных
     * 
     * @param dataSource источник данных с пулом соединений
     * @return настроенный JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Закрывает пул соединений при остановке приложения
     */
    @PreDestroy
    public void closeDataSource() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при закрытии пула соединений", e);
            }
        }
    }
}
