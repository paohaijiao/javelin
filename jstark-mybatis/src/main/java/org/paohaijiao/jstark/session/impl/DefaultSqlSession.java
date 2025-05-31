package org.paohaijiao.jstark.session.impl;

import org.paohaijiao.jstark.session.JSqlSession;
import org.paohaijiao.jstark.support.JMappedStatement;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class DefaultSqlSession extends BaseSqlSession implements JSqlSession {
    private final Connection connection;
    private final Map<String, JMappedStatement> mappedStatements;
    private boolean autoCommit;
    public DefaultSqlSession(Connection connection,
                             Map<String, JMappedStatement> mappedStatements,
                             boolean autoCommit) {
        this.connection = connection;
        this.mappedStatements = mappedStatements;
        this.autoCommit = autoCommit;
    }
    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> results = selectList(statement, parameter);
        if (results.size() == 1) {
            return results.get(0);
        } else if (results.size() > 1) {
            throw new RuntimeException("Expected one result (or null) but found: " + results.size());
        }
        return null;
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        JMappedStatement ms = mappedStatements.get(statement);
        if (ms == null) {
            throw new RuntimeException("Unknown statement: " + statement);
        }
        try (PreparedStatement ps = connection.prepareStatement(ms.getSql())) {
            try (ResultSet rs = ps.executeQuery()) {
                return (List<E>) resultSetToObjects(rs, ms.getResultType(), ms.getResultGenericType());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + statement, e);
        }
    }



    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        JMappedStatement ms = mappedStatements.get(statement);
        if (ms == null) {
            throw new RuntimeException("Unknown statement: " + statement);
        }
        try (PreparedStatement ps = connection.prepareStatement(ms.getSql())) {
            if (parameter != null) {
                //todo
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update: " + statement, e);
        }
    }

    @Override
    public int delete(String statement, Object parameter) {
        return 0;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }
}
