package com.paohaijiao.javelin.core;

import com.paohaijiao.javelin.mapper.JLambdaMapper;
import com.paohaijiao.javelin.mapper.JLambdaQuery;
import com.paohaijiao.javelin.mapper.JLambdaUpdate;
import com.paohaijiao.javelin.session.JSqlSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JLambdaMapperImpl<T> implements JLambdaMapper<T> {
    private final Class<T> entityClass;
    private final JSqlSession sqlSession;

    public JLambdaMapperImpl(Class<T> entityClass, JSqlSession sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(T entity) {
        String sql = new LambdaInsertImpl<>(entityClass, sqlSession).buildInsertSQL();
        System.out.println("insert sql:" + sql);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("entity", entity);
        return sqlSession.insert(null, paramMap);
    }

    @Override
    public int updateById(T entity) {
        return new LambdaUpdateImpl<>(entityClass, sqlSession).updateById();
    }

    @Override
    public int deleteById(Serializable id) {
        return new LambdaDeleteImpl<>(entityClass, sqlSession).deleteById(id);
    }

    @Override
    public T selectById(Serializable id) {
        return new JLambdaQueryImpl<>(entityClass, sqlSession).selectById(id);

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
