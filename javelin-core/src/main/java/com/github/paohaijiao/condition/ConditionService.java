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
     * 基本条件判断 - 使用Condition
     */
    boolean testWith(T value, Condition<T> condition);

    /**
     * 基本条件判断 - 使用Predicate
     */
    boolean testWithPredicate(T value, Predicate<T> predicate);
    /**
     * 条件映射 - 使用Condition
     */
    Optional<R> mapIf(T value, Condition<T> condition, Function<T, R> mapper);

    /**
     * 条件映射 - 使用Predicate
     */
    Optional<R> mapIfWithPredicate(T value, Predicate<T> predicate, Function<T, R> mapper);
    /**
     * 条件消费 - 使用Condition
     */
    void acceptIf(T value, Condition<T> condition, Consumer<T> action);

    /**
     * 条件消费 - 使用Predicate
     */
    void acceptIfWithPredicate(T value, Predicate<T> predicate, Consumer<T> action);
    /**
     * 条件获取 - 使用Condition
     */
    R getIf(T value, Condition<T> condition, Supplier<R> supplier, R defaultValue);

    /**
     * 条件获取 - 使用Predicate
     */
    R getIfWithPredicate(T value, Predicate<T> predicate, Supplier<R> supplier, R defaultValue);


    /**
     * 多条件组合 - 全满足(Condition)
     */
    boolean testAll(T value, Condition<T>... conditions);

    /**
     * 多条件组合 - 全满足(Predicate)
     */
    boolean testAllWithPredicate(T value, Predicate<T>... predicates);

    /**
     * 多条件组合 - 任一满足(Condition)
     */
    boolean testAny(T value, Condition<T>... conditions);

    /**
     * 多条件组合 - 任一满足(Predicate)
     */
    boolean testAnyWithPredicate(T value, Predicate<T>... predicates);


    /**
     * 验证器模式
     */
    Validator<T> validator(T value);

    /**
     * 条件链构建
     */
    ConditionChain<T, R> chain(T value);
}