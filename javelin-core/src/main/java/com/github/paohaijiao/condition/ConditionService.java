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
 * 条件判断服务接口
 */
public interface ConditionService<T, R> {

    /**
     * 基本条件判断
     * @param value
     * @param condition
     * @return
     */
    boolean test(T value, Condition<T> condition);

    boolean test(T value, Predicate<T> predicate);

    /**
     * 条件映射
     * @param value
     * @param condition
     * @param mapper
     * @return
     */
    Optional<R> mapIf(T value, Condition<T> condition, Function<T, R> mapper);

    Optional<R> mapIf(T value, Predicate<T> predicate, Function<T, R> mapper);

    /**
     * 条件消费
     * @param value
     * @param condition
     * @param action
     */
    void acceptIf(T value, Condition<T> condition, Consumer<T> action);

    void acceptIf(T value, Predicate<T> predicate, Consumer<T> action);

    /**
     * 条件获取
     * @param value
     * @param condition
     * @param supplier
     * @param defaultValue
     * @return
     */
    R getIf(T value, Condition<T> condition, Supplier<R> supplier, R defaultValue);

    R getIf(T value, Predicate<T> predicate, Supplier<R> supplier, R defaultValue);

    /**
     * 多条件组合
     * @param value
     * @param conditions
     * @return
     */
    boolean testAll(T value, Condition<T>... conditions);

    boolean testAll(T value, Predicate<T>... predicates);

    boolean testAny(T value, Condition<T>... conditions);

    boolean testAny(T value, Predicate<T>... predicates);

    /**
     * 验证器模式
     * @param value
     * @return
     */
    Validator<T> validator(T value);

    // 条件链构建
    ConditionChain<T, R> chain(T value);
}