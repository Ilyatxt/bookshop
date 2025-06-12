package com.example.bookshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Пул соединений с базой данных
 */
public class ConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    private static final int DEFAULT_POOL_SIZE = 10;
    private static final long CONNECTION_TIMEOUT = 10; // в секундах

    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    private final BlockingQueue<Connection> connectionPool;
    private final List<Connection> usedConnections = new ArrayList<>();

    /**
     * Создает пул соединений с указанными параметрами
     *
     * @param url URL базы данных
     * @param username имя пользователя
     * @param password пароль
     * @param poolSize размер пула соединений
     */
    public ConnectionPool(String url, String username, String password, int poolSize) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.poolSize = poolSize;
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);

        try {
            Class.forName("org.postgresql.Driver");
            initializeConnectionPool();
        } catch (ClassNotFoundException e) {
            log.error("Не удалось загрузить драйвер PostgreSQL", e);
            throw new RuntimeException("Не удалось загрузить драйвер PostgreSQL", e);
        }
    }

    /**
     * Создает пул соединений с размером по умолчанию
     *
     * @param url URL базы данных
     * @param username имя пользователя
     * @param password пароль
     */
    public ConnectionPool(String url, String username, String password) {
        this(url, username, password, DEFAULT_POOL_SIZE);
    }

    /**
     * Инициализирует пул соединений
     */
    private void initializeConnectionPool() {
        for (int i = 0; i < poolSize; i++) {
            try {
                connectionPool.add(createConnection());
            } catch (SQLException e) {
                log.error("Ошибка при создании соединения с БД", e);
            }
        }
        log.info("Пул соединений инициализирован. Размер пула: {}", connectionPool.size());
    }

    /**
     * Создает новое соединение с базой данных
     *
     * @return новое соединение
     * @throws SQLException если произошла ошибка при создании соединения
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Получает соединение из пула
     *
     * @return соединение с базой данных
     * @throws SQLException если не удалось получить соединение
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = connectionPool.poll(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            if (connection == null) {
                log.error("Превышено время ожидания соединения");
                throw new SQLException("Превышено время ожидания соединения");
            }

            if (connection.isClosed()) {
                log.warn("Соединение закрыто, создаем новое");
                connection = createConnection();
            }

            usedConnections.add(connection);
            log.debug("Получено соединение из пула. Доступно: {}, Используется: {}", 
                    connectionPool.size(), usedConnections.size());
            return connection;
        } catch (InterruptedException e) {
            log.error("Прерывание при ожидании соединения", e);
            Thread.currentThread().interrupt();
            throw new SQLException("Не удалось получить соединение из пула", e);
        }
    }

    /**
     * Возвращает соединение в пул
     *
     * @param connection соединение для возврата в пул
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            usedConnections.remove(connection);
            try {
                if (!connection.isClosed()) {
                    if (!connection.getAutoCommit()) {
                        connection.setAutoCommit(true);
                    }
                    connectionPool.offer(connection);
                    log.debug("Соединение возвращено в пул. Доступно: {}, Используется: {}", 
                            connectionPool.size(), usedConnections.size());
                } else {
                    log.warn("Попытка вернуть закрытое соединение");
                }
            } catch (SQLException e) {
                log.error("Ошибка при возврате соединения в пул", e);
            }
        }
    }

    /**
     * Закрывает все соединения в пуле
     */
    public void closeAllConnections() {
        closeConnections(usedConnections);
        closeConnections(new ArrayList<>(connectionPool));
        log.info("Все соединения закрыты");
    }

    /**
     * Закрывает список соединений
     *
     * @param connections список соединений для закрытия
     */
    private void closeConnections(List<Connection> connections) {
        for (Connection connection : connections) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("Ошибка при закрытии соединения", e);
            }
        }
        connections.clear();
    }

    /**
     * @return текущий размер пула соединений
     */
    public int getPoolSize() {
        return connectionPool.size() + usedConnections.size();
    }

    /**
     * @return количество свободных соединений
     */
    public int getAvailableConnections() {
        return connectionPool.size();
    }

    /**
     * @return количество используемых соединений
     */
    public int getUsedConnections() {
        return usedConnections.size();
    }
}
