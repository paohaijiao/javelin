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
package com.paohaijiao.javelin.lamda;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;
public class JLambda {

    private JLambda() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 谓词(Predicate)工具方法 - 判断集合中是否存在满足条件的元素
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().anyMatch(predicate);
    }

    /**
     * 谓词(Predicate)工具方法 - 过滤集合
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 函数(Function)工具方法 - 映射集合
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        return collection.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 消费(Consumer)工具方法 - 对集合中每个元素执行操作
     */
    public static <T> void forEach(Collection<T> collection, Consumer<T> consumer) {
        collection.forEach(consumer);
    }

    /**
     * 分组工具方法 - 按照指定分类函数分组
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> classifier) {
        return collection.stream().collect(Collectors.groupingBy(classifier));
    }

    /**
     * 去重工具方法 - 按照指定函数提取特征去重
     */
    public static <T, K> List<T> distinctBy(Collection<T> collection, Function<T, K> keyExtractor) {
        return collection.stream()
                .filter(distinctByKey(keyExtractor))
                .collect(Collectors.toList());
    }

    private static <T, K> Predicate<T> distinctByKey(Function<T, K> keyExtractor) {
        Set<K> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * 链式Predicate - 多个Predicate的与操作
     */
    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<T>... predicates) {
        return Arrays.stream(predicates).reduce(Predicate::and).orElse(t -> true);
    }

    /**
     * 链式Predicate - 多个Predicate的或操作
     */
    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<T>... predicates) {
        return Arrays.stream(predicates).reduce(Predicate::or).orElse(t -> false);
    }

    /**
     * 将BiFunction转换为带两个参数的Function
     */
    public static <T, U, R> Function<T, Function<U, R>> curry(BiFunction<T, U, R> biFunction) {
        return t -> u -> biFunction.apply(t, u);
    }

    /**
     * 将两个Consumer组合成一个BiConsumer
     */
    public static <T, U> BiConsumer<T, U> toBiConsumer(Consumer<T> first, Consumer<U> second) {
        return (t, u) -> {
            first.accept(t);
            second.accept(u);
        };
    }

    /**
     * 将多个 Consumer 组合成一个
     */
    @SafeVarargs
    public static <T> Consumer<T> combineConsumers(Consumer<T>... consumers) {
        return t -> Arrays.stream(consumers).forEach(c -> c.accept(t));
    }


    /**
     * 创建一个带缓存的 Supplier
     */
    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return new Supplier<T>() {
            private volatile boolean initialized;
            private T value;
            @Override
            public T get() {
                if (!initialized) {
                    synchronized (this) {
                        if (!initialized) {
                            value = supplier.get();
                            initialized = true;
                        }
                    }
                }
                return value;
            }
        };
    }


    /**
     * 将两个集合按索引合并
     */
    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> zipper) {
        int size = Math.min(list1.size(), list2.size());
        List<R> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(zipper.apply(list1.get(i), list2.get(i)));
        }
        return result;
    }

    /**
     * 按条件分区集合
     */
    public static <T> Map<Boolean, List<T>> partition(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().collect(Collectors.partitioningBy(predicate));
    }

    /**
     * 将集合转换为查找Map
     */
    public static <T, K> Map<K, T> toMap(Collection<T> collection, Function<T, K> keyMapper) {
        return collection.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    /**
     * 并行处理集合
     */
    public static <T, R> List<R> parallelMap(Collection<T> collection, Function<T, R> mapper) {
        return collection.parallelStream().map(mapper).collect(Collectors.toList());
    }

    // ================ 空安全操作 ================

    /**
     * 空安全的 Optional 转换
     */
    public static <T, R> Function<T, Optional<R>> safeMap(Function<T, R> mapper) {
        return t -> Optional.ofNullable(t).map(mapper);
    }

    /**
     * 空安全的 BiFunction
     */
    public static <T, U, R> BiFunction<T, U, R> safeBiFunction(BiFunction<T, U, R> function, R defaultValue) {
        return (t, u) -> {
            try {
                return function.apply(t, u);
            } catch (NullPointerException e) {
                return defaultValue;
            }
        };
    }

    /**
     * 将可能抛出异常的 Function 包装为安全的
     */
    public static <T, R> Function<T, Optional<R>> tryFunction(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return Optional.ofNullable(function.apply(t));
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    /**
     * 将可能抛出异常的 Supplier 包装为安全的
     */
    public static <T> Supplier<Optional<T>> trySupplier(ThrowingSupplier<T> supplier) {
        return () -> {
            try {
                return Optional.ofNullable(supplier.get());
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * 带计时的 Runnable
     */
    public static Runnable timedRunnable(Runnable task, Consumer<Long> timeConsumer) {
        return () -> {
            long start = System.currentTimeMillis();
            task.run();
            timeConsumer.accept(System.currentTimeMillis() - start);
        };
    }

    /**
     * 带计时的 Supplier
     */
    public static <T> Supplier<T> timedSupplier(Supplier<T> supplier, Consumer<Long> timeConsumer) {
        return () -> {
            long start = System.currentTimeMillis();
            T result = supplier.get();
            timeConsumer.accept(System.currentTimeMillis() - start);
            return result;
        };
    }

    /**
     * 重试操作
     */
    public static <T> T retry(int maxAttempts, Supplier<T> supplier, Predicate<Exception> retryCondition) {
        int attempts = 0;
        while (true) {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (++attempts >= maxAttempts || !retryCondition.test(e)) {
                    throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 带超时的 Supplier
     */
    public static <T> Optional<T> withTimeout(Supplier<T> supplier, long timeout, TimeUnit unit) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(supplier::get);

        try {
            return Optional.ofNullable(future.get(timeout, unit));
        } catch (TimeoutException e) {
            future.cancel(true);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            executor.shutdownNow();
        }
    }

}
