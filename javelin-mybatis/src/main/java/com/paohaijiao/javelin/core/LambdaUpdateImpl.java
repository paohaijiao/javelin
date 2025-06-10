package com.paohaijiao.javelin.core;

import com.paohaijiao.javelin.function.JSFunction;
import com.paohaijiao.javelin.mapper.JLambdaQuery;
import com.paohaijiao.javelin.mapper.JLambdaUpdate;
import com.paohaijiao.javelin.session.JSqlSession;

public class LambdaUpdateImpl <T> extends JLambdaBaseImpl<T> implements JLambdaUpdate<T> {
    public LambdaUpdateImpl(Class<T> entityClass, JSqlSession sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }

    @Override
    public JLambdaUpdate<T> set(JSFunction<T, ?> column, Object value) {
        return null;
    }

    @Override
    public JLambdaUpdate<T> eq(JSFunction<T, ?> column, Object value) {
        return null;
    }

    @Override
    public int execute() {
        return 0;
    }
}
