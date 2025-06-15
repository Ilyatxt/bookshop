package com.example.bookshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PreDestroy;
import com.example.bookshop.config.ConnectionPool;
import com.example.bookshop.config.ConnectionPoolDataSource;

import javax.sql.DataSource;

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

    private ConnectionPool connectionPool;

    /**
     * Создает и настраивает {@link ConnectionPool} и соответствующий {@link DataSource}.
     *
     * @return источник данных, использующий пул соединений
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        connectionPool = new ConnectionPool(dbUrl, dbUsername, dbPassword, maxTotal);
        return new ConnectionPoolDataSource(connectionPool);
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
    public void closeConnectionPool() {
        if (connectionPool != null) {
            connectionPool.closeAllConnections();
        }
    }
}
