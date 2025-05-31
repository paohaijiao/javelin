package org.paohaijiao.jstark.session;

import java.util.List;

public interface JSqlSession {
    <T> T selectOne(String statement, Object parameter);
    <E> List<E> selectList(String statement, Object parameter);
    int insert(String statement, Object parameter);
    int update(String statement, Object parameter);
    int delete(String statement, Object parameter);
    void commit();
    void rollback();
    void close();
}
