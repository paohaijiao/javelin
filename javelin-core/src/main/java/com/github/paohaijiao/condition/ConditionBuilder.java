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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConditionBuilder {

    private ConditionBuilder() {
    }

    /**
     * 创建组合条件
     */
    @SafeVarargs
    public static <T> Condition<T> allOf(Condition<T>... conditions) {
        return t -> Arrays.stream(conditions).allMatch(c -> c.test(t));
    }

    @SafeVarargs
    public static <T> Condition<T> anyOf(Condition<T>... conditions) {
        return t -> Arrays.stream(conditions).anyMatch(c -> c.test(t));
    }

    public static <T> Condition<T> noneOf(Condition<T> condition) {
        return condition.negate();
    }

    /**
     * 创建带类型转换的条件
     */
    public static <T, U> Condition<T> withConversion(Function<T, U> converter, Condition<U> condition) {
        return t -> condition.test(converter.apply(t));
    }

    public static <T, U> Condition<T> withConversion(Function<T, U> converter, Predicate<U> predicate) {
        return withConversion(converter, Condition.of(predicate));
    }

    /**
     * 流式条件构建器
     */
    public static class FluentBuilder<T> {
        private final List<Condition<T>> conditions = new ArrayList<>();

        private FluentBuilder() {
        }

        public static <T> FluentBuilder<T> create() {
            return new FluentBuilder<>();
        }

        public FluentBuilder<T> add(Condition<T> condition) {
            conditions.add(condition);
            return this;
        }

        public FluentBuilder<T> add(Predicate<T> predicate) {
            conditions.add(Condition.of(predicate));
            return this;
        }

        public FluentBuilder<T> addNotNull() {
            conditions.add(Condition.nonNull());
            return this;
        }

        public <U> FluentBuilder<T> addWithConversion(
                Function<T, U> converter,
                Condition<U> condition) {
            conditions.add(withConversion(converter, condition));
            return this;
        }

        public <U> FluentBuilder<T> addWithConversion(
                Function<T, U> converter,
                Predicate<U> predicate) {
            conditions.add(withConversion(converter, predicate));
            return this;
        }

        public Condition<T> build() {
            return allOf(conditions.toArray(new Condition[0]));
        }

        public Condition<T> buildAny() {
            return anyOf(conditions.toArray(new Condition[0]));
        }
    }
}
