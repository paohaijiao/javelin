package org.paohaijiao.jstark.session.impl;

import org.paohaijiao.jstark.session.JSqlSession;
import org.paohaijiao.jstark.session.JSqlSessionFactory;
import org.paohaijiao.jstark.support.JMappedStatement;

import javax.security.auth.login.Configuration;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DefaultSqlSessionFactory implements JSqlSessionFactory {
    private final DataSource dataSource;
    private final Map<String, JMappedStatement> mappedStatements;

    public DefaultSqlSessionFactory(DataSource dataSource,
                                    Map<String, JMappedStatement> mappedStatements) {
        this.dataSource = dataSource;
        this.mappedStatements = mappedStatements;
    }

    @Override
    public JSqlSession openSession() {
        try {
            //dataSource.getConnection()
            return new DefaultSqlSession(dataSource.getConnection(), mappedStatements, false);
        } catch (Exception e) {
            throw new RuntimeException("Cannot open connection", e);
        }
    }

    @Override
    public JSqlSession openSession(boolean autoCommit) {
        try {
            return new DefaultSqlSession(dataSource.getConnection(), mappedStatements, autoCommit);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot open connection", e);
        }
    }

    @Override
    public JSqlSession openSession(Connection connection) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }
}
