package org.paohaijiao.jstark.core;

import org.paohaijiao.jstark.mapper.JLambdaMapper;
import org.paohaijiao.jstark.mapper.JLambdaQuery;
import org.paohaijiao.jstark.mapper.JLambdaUpdate;
import org.paohaijiao.jstark.session.JSqlSession;

import java.io.Serializable;

public class JLambdaMapperImpl<T> implements JLambdaMapper<T> {
    private final Class<T> entityClass;
    private final JSqlSession sqlSession;

    public JLambdaMapperImpl(Class<T> entityClass, JSqlSession sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(T entity) {
        return 0;
    }

    @Override
    public int updateById(T entity) {
        return 0;
    }

    @Override
    public int deleteById(Serializable id) {
        return 0;
    }

    @Override
    public T selectById(Serializable id) {
        return null;
    }

    @Override
    public JLambdaQuery<T> query() {
        return new JLambdaQueryImpl<>(entityClass, sqlSession);
    }

    @Override
    public JLambdaUpdate<T> update() {
//        return new LambdaUpdateImpl<>(entityClass, sqlSession);
        return null;
    }
}
