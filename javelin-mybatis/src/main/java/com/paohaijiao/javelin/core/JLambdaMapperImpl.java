package com.paohaijiao.javelin.core;

import com.paohaijiao.javelin.mapper.JLambdaMapper;
import com.paohaijiao.javelin.mapper.JLambdaQuery;
import com.paohaijiao.javelin.mapper.JLambdaUpdate;
import com.paohaijiao.javelin.session.JSqlSession;

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
        return new LambdaUpdateImpl<>(entityClass, sqlSession);
    }
}
