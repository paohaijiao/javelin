package com.paohaijiao.javelin.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;
public class LambdaUtils {
    private LambdaUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================ 基础函数工具 ================

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

    // ================ 集合操作增强 ================

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

    // ================ 异常处理 ================

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

    // ================ 计时操作 ================

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

    // ================ 其他实用方法 ================

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
