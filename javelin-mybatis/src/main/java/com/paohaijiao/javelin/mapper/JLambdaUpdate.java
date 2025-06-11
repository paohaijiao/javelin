package com.paohaijiao.javelin.mapper;

import com.paohaijiao.javelin.function.JSFunction;

import java.util.Collection;

public interface JLambdaUpdate<T>{
    JLambdaUpdate<T> set(JSFunction<T, ?> column, Object value);
    JLambdaUpdate<T> eq(JSFunction<T, ?> column, Object value);


    public JLambdaUpdate<T> ne(JSFunction<T, ?> column, Object value) ;
    public JLambdaUpdate<T> gt(JSFunction<T, ?> column, Object value) ;

    public JLambdaUpdate<T> ge(JSFunction<T, ?> column, Object value);
    public JLambdaUpdate<T> lt(JSFunction<T, ?> column, Object value);

    public JLambdaUpdate<T> le(JSFunction<T, ?> column, Object value) ;
    public JLambdaUpdate<T> like(JSFunction<T, ?> column, String value);
    public JLambdaUpdate<T> in(JSFunction<T, ?> column, Collection<?> values) ;
    int execute();
}
