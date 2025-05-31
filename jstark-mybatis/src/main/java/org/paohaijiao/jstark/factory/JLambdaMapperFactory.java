package org.paohaijiao.jstark.factory;

import org.paohaijiao.jstark.core.JLambdaMapperImpl;
import org.paohaijiao.jstark.mapper.JLambdaMapper;
import org.paohaijiao.jstark.session.JSqlSessionFactory;

public class JLambdaMapperFactory {
    private final JSqlSessionFactory sqlSessionFactory;

    public JLambdaMapperFactory(JSqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public <T> JLambdaMapper<T> createMapper(Class<T> entityClass) {
        return new JLambdaMapperImpl<>(entityClass, sqlSessionFactory.openSession());
    }
}
