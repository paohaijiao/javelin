package org.paohaijiao.jstark.datasource.impl;

import org.paohaijiao.jstark.datasource.JPoolDataSource;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JSimplePooledDataSource implements JPoolDataSource {
    private static final Integer defaultPooledMaxSize=10;
    private final JMySqlBaseDataSource dataSource;
    private final BlockingQueue<Connection> pool;
    private final int maxSize;

    public JSimplePooledDataSource(String driver, String url,
                                   String username, String password,
                                   int poolSize) {
        this.dataSource = new JMySqlBaseDataSource(driver, url, username, password);
        this.pool = new LinkedBlockingQueue<>(poolSize);
        this.maxSize = poolSize;
        initializePool();
    }
    public JSimplePooledDataSource(String driver, String url,
                                   String username, String password) {
        this.dataSource = new JMySqlBaseDataSource(driver, url, username, password);
        this.pool = new LinkedBlockingQueue<>(defaultPooledMaxSize);
        this.maxSize = defaultPooledMaxSize;
        initializePool();
    }
    private void initializePool() {
        for (int i = 0; i < maxSize; i++) {
            pool.add(createProxyConnection());
        }
    }

    private Connection createProxyConnection() {
        try {
            Connection realConn = dataSource.getDataSource().getConnection();
            return (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> {
                        if ("close".equals(method.getName())) {
                            // 拦截close方法，将连接放回池中
                            pool.put((Connection) proxy);
                            return null;
                        }
                        return method.invoke(realConn, args);
                    });
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create connection", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }

}
