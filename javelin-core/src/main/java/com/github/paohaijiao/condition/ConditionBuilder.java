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

/**
 * 条件构建器
 * 注意：为避免泛型擦除导致的重载冲突，Condition和Predicate使用不同的方法名
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 条件构建器
 * 注意：为避免泛型擦除导致的重载冲突，Condition和Predicate使用不同的方法名
 */
public class ConditionBuilder {

    private ConditionBuilder() {
    }


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
    @SafeVarargs
    public static <T> Condition<T> allOfPredicates(Predicate<T>... predicates) {
        Condition<T>[] conditions = Arrays.stream(predicates)
                .map(Condition::of)
                .toArray(Condition[]::new);
        return allOf(conditions);
    }

    @SafeVarargs
    public static <T> Condition<T> anyOfPredicates(Predicate<T>... predicates) {
        Condition<T>[] conditions = Arrays.stream(predicates)
                .map(Condition::of)
                .toArray(Condition[]::new);
        return anyOf(conditions);
    }

    public static <T> Condition<T> noneOfPredicate(Predicate<T> predicate) {
        return Condition.of(predicate).negate();
    }
    public static <T, U> Condition<T> withConversion(Function<T, U> converter, Condition<U> condition) {
        return t -> {
            U converted = converter.apply(t);
            return condition.test(converted);
        };
    }

    public static <T, U> Condition<T> withConversionFromPredicate(Function<T, U> converter, Predicate<U> predicate) {
        return withConversion(converter, Condition.of(predicate));
    }

    public static class FluentBuilder<T> {
        private final List<Condition<T>> conditions = new ArrayList<>();

        private FluentBuilder() {
        }

        public static <T> FluentBuilder<T> create() {
            return new FluentBuilder<>();
        }
        public FluentBuilder<T> addCondition(Condition<T> condition) {
            conditions.add(condition);
            return this;
        }

        public FluentBuilder<T> addPredicate(Predicate<T> predicate) {
            conditions.add(Condition.of(predicate));
            return this;
        }

        public FluentBuilder<T> addNotNull() {
            conditions.add(Condition.nonNull());
            return this;
        }

        public FluentBuilder<T> addIsNull() {
            conditions.add(Condition.isNull());
            return this;
        }

        public FluentBuilder<T> addEqualTo(T target) {
            conditions.add(Condition.equalTo(target));
            return this;
        }

        public <U> FluentBuilder<T> addWithConversion(
                Function<T, U> converter,
                Condition<U> condition) {
            conditions.add(withConversion(converter, condition));
            return this;
        }

        public <U> FluentBuilder<T> addWithConversionFromPredicate(
                Function<T, U> converter,
                Predicate<U> predicate) {
            conditions.add(withConversionFromPredicate(converter, predicate));
            return this;
        }

        /**
         * 开始链式添加转换条件 - 返回中间构建器
         */
        public <U> ConvertedConditionBuilder<T, U> addConverted(Function<T, U> converter) {
            return new ConvertedConditionBuilder<>(this, converter);
        }

        public Condition<T> buildAnd() {
            return allOf(conditions.toArray(new Condition[0]));
        }

        public Condition<T> buildOr() {
            return anyOf(conditions.toArray(new Condition[0]));
        }

        public Condition<T> buildNone() {
            return buildAnd().negate();
        }

        public int size() {
            return conditions.size();
        }

        public FluentBuilder<T> clear() {
            conditions.clear();
            return this;
        }
    }

    /**
     * 转换条件构建器（用于链式调用）
     * 注意：这个类的方法返回FluentBuilder<T>，而不是自身
     */
    public static class ConvertedConditionBuilder<T, U> {
        private final FluentBuilder<T> parent;
        private final Function<T, U> converter;

        private ConvertedConditionBuilder(FluentBuilder<T> parent, Function<T, U> converter) {
            this.parent = parent;
            this.converter = converter;
        }

        /**
         * 应用Condition条件并返回父构建器
         */
        public FluentBuilder<T> to(Condition<U> condition) {
            parent.addWithConversion(converter, condition);
            return parent;
        }

        /**
         * 应用Predicate条件并返回父构建器
         */
        public FluentBuilder<T> toPredicate(Predicate<U> predicate) {
            parent.addWithConversionFromPredicate(converter, predicate);
            return parent;
        }
    }

    public static <T> FluentBuilder<T> and() {
        return FluentBuilder.create();
    }

    public static <T> FluentBuilder<T> or() {
        return FluentBuilder.create();
    }

    public static <T> Condition<T> of(Condition<T> condition) {
        return condition;
    }

    public static <T> Condition<T> ofPredicate(Predicate<T> predicate) {
        return Condition.of(predicate);
    }
}