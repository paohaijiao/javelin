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

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 通用条件接口
 */
@FunctionalInterface
public interface Condition<T> {

    static <T> Condition<T> of(Predicate<T> predicate) {
        return predicate::test;
    }

    static <T> Condition<T> alwaysTrue() {
        return t -> true;
    }

    static <T> Condition<T> alwaysFalse() {
        return t -> false;
    }

    static <T> Condition<T> isNull() {
        return Objects::isNull;
    }

    static <T> Condition<T> nonNull() {
        return Objects::nonNull;
    }

    static <T> Condition<T> equalTo(T target) {
        return t -> Objects.equals(t, target);
    }

    boolean test(T value);

    default Condition<T> and(Condition<T> other) {
        return t -> this.test(t) && other.test(t);
    }

    default Condition<T> and(Predicate<T> other) {
        return and(of(other));
    }

    default Condition<T> or(Condition<T> other) {
        return t -> this.test(t) || other.test(t);
    }

    default Condition<T> or(Predicate<T> other) {
        return or(of(other));
    }

    default Condition<T> negate() {
        return t -> !this.test(t);
    }

    /**
     * 条件转换
     *
     * @param function
     * @param <R>
     * @return
     */
    default <R> Condition<R> compose(Function<R, T> function) {
        return r -> this.test(function.apply(r));
    }

    /**
     * 链式构建器
     *
     * @param value
     * @return
     */
    default ConditionalChain<T> chain(T value) {
        return new ConditionalChain<>(this, value);
    }
}