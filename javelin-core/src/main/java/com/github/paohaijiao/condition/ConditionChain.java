package com.github.paohaijiao.condition;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 条件链接口
 */
public interface ConditionChain<T, R> {

    ConditionChain<T, R> when(Condition<T> condition);

    ConditionChain<T, R> when(Predicate<T> predicate);

    ConditionChain<T, R> then(Consumer<T> action);

    Optional<R> thenMap(Function<T, R> mapper);

    R orElse(R defaultValue);

    R orElseGet(Supplier<R> supplier);

    <X extends Throwable> R orElseThrow(Supplier<X> exceptionSupplier) throws X;
}
