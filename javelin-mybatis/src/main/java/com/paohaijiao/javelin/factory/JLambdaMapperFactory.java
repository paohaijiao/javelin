package com.paohaijiao.javelin.factory;

import com.paohaijiao.javelin.core.JLambdaMapperImpl;
import com.paohaijiao.javelin.mapper.JLambdaMapper;
import com.paohaijiao.javelin.session.JSqlSessionFactory;

public class JLambdaMapperFactory {
    private final JSqlSessionFactory sqlSessionFactory;

    public JLambdaMapperFactory(JSqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public <T> JLambdaMapper<T> createMapper(Class<T> entityClass) {
        return new JLambdaMapperImpl<>(entityClass, sqlSessionFactory.openSession());
    }
}
