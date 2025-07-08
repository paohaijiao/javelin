package com.github.paohaijiao.function;
@FunctionalInterface
public interface JBiConsumer<T, U> {

    void accept(T t, U u);

}
