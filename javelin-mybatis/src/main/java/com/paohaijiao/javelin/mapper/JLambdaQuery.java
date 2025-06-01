package com.paohaijiao.javelin.mapper;

import com.paohaijiao.javelin.function.JSFunction;

import java.util.Collection;
import java.util.List;

public interface JLambdaQuery<T>{
    JLambdaQuery<T> eq(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> ne(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> gt(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> ge(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> lt(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> le(JSFunction<T, ?> column, Object value);
    JLambdaQuery<T> like(JSFunction<T, ?> column, String value);
    JLambdaQuery<T> in(JSFunction<T, ?> column, Collection<?> values);
    JLambdaQuery<T> orderByAsc(JSFunction<T, ?> column);
    JLambdaQuery<T> orderByDesc(JSFunction<T, ?> column);

    List<T> list();
    T one();
    long count();
}
