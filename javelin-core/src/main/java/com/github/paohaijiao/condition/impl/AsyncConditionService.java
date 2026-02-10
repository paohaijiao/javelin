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
package com.github.paohaijiao.condition.impl;

/**
 * packageName com.github.paohaijiao.condition.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/2/10
 */

import com.github.paohaijiao.condition.Condition;
import com.github.paohaijiao.condition.SimpleConditionService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AsyncConditionService<T, R> extends SimpleConditionService<T, R> {

    private final ExecutorService executor;

    public AsyncConditionService() {
        this.executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
    }

    public AsyncConditionService(ExecutorService executor) {
        this.executor = executor;
    }

    public CompletableFuture<Boolean> testAsync(T value, Condition<T> condition) {
        return CompletableFuture.supplyAsync(
                () -> super.test(value, condition), executor
        );
    }

    public CompletableFuture<Boolean> testAsync(T value, Predicate<T> predicate) {
        return CompletableFuture.supplyAsync(
                () -> super.test(value, predicate), executor
        );
    }

    public CompletableFuture<Optional<R>> mapIfAsync(T value, Condition<T> condition, Function<T, R> mapper) {
        return CompletableFuture.supplyAsync(
                () -> super.mapIf(value, condition, mapper), executor
        );
    }

    public CompletableFuture<Optional<R>> mapIfAsync(T value, Predicate<T> predicate, Function<T, R> mapper) {
        return CompletableFuture.supplyAsync(
                () -> super.mapIf(value, predicate, mapper), executor
        );
    }

    public CompletableFuture<Void> acceptIfAsync(T value, Condition<T> condition, Consumer<T> action) {
        return CompletableFuture.runAsync(
                () -> super.acceptIf(value, condition, action), executor
        );
    }

    public CompletableFuture<Void> acceptIfAsync(T value, Predicate<T> predicate, Consumer<T> action) {
        return CompletableFuture.runAsync(
                () -> super.acceptIf(value, predicate, action), executor
        );
    }

    public CompletableFuture<R> getIfAsync(T value, Condition<T> condition, Supplier<R> supplier, R defaultValue) {
        return CompletableFuture.supplyAsync(
                () -> super.getIf(value, condition, supplier, defaultValue), executor
        );
    }

    public CompletableFuture<R> getIfAsync(T value, Predicate<T> predicate, Supplier<R> supplier, R defaultValue) {
        return CompletableFuture.supplyAsync(
                () -> super.getIf(value, predicate, supplier, defaultValue), executor
        );
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
