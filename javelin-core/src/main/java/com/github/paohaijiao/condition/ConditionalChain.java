/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.condition;

/**
 * packageName com.github.paohaijiao.condition
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/2/10
 */

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 条件链式构建器
 */
public class ConditionalChain<T> {

    private final Condition<T> condition;

    private final T value;

    private boolean conditionMet;

    public ConditionalChain(Condition<T> condition, T value) {
        this.condition = condition;
        this.value = value;
        this.conditionMet = condition.test(value);
    }

    /**
     * 添加额外条件
     *
     * @param additionalCondition
     * @return
     */
    public ConditionalChain<T> and(Condition<T> additionalCondition) {
        if (conditionMet) {
            conditionMet = additionalCondition.test(value);
        }
        return this;
    }

    public ConditionalChain<T> and(Predicate<T> predicate) {
        return and(Condition.of(predicate));
    }

    public ConditionalChain<T> or(Condition<T> alternativeCondition) {
        if (!conditionMet) {
            conditionMet = alternativeCondition.test(value);
        }
        return this;
    }

    public ConditionalChain<T> or(Predicate<T> predicate) {
        return or(Condition.of(predicate));
    }

    /**
     * 执行动作
     *
     * @param action
     * @return
     */
    public ConditionalChain<T> then(Consumer<T> action) {
        if (conditionMet && value != null) {
            action.accept(value);
        }
        return this;
    }

    public <R> Optional<R> map(Function<T, R> mapper) {
        if (conditionMet && value != null) {
            return Optional.ofNullable(mapper.apply(value));
        }
        return Optional.empty();
    }

    /**
     * 获取结果
     *
     * @return
     */
    public Optional<T> result() {
        return conditionMet ? Optional.ofNullable(value) : Optional.empty();
    }

    public T orElse(T defaultValue) {
        return conditionMet && value != null ? value : defaultValue;
    }

    public T orElseGet(Supplier<T> supplier) {
        return conditionMet && value != null ? value : supplier.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (!conditionMet || value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }

    public boolean isMet() {
        return conditionMet;
    }
}
