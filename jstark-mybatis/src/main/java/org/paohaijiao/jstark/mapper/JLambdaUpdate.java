package org.paohaijiao.jstark.mapper;

import org.paohaijiao.jstark.function.JSFunction;

public interface JLambdaUpdate<T>{
    JLambdaUpdate<T> set(JSFunction<T, ?> column, Object value);
    JLambdaUpdate<T> eq(JSFunction<T, ?> column, Object value);
    int execute();
}
