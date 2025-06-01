package com.paohaijiao.javelin.mapper;

import com.paohaijiao.javelin.function.JSFunction;

public interface JLambdaUpdate<T>{
    JLambdaUpdate<T> set(JSFunction<T, ?> column, Object value);
    JLambdaUpdate<T> eq(JSFunction<T, ?> column, Object value);
    int execute();
}
