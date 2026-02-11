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
import com.github.paohaijiao.condition.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SimpleConditionService<T, R> implements ConditionService<T, R> {


    @Override
    public boolean testWith(T value, Condition<T> condition) {
        return condition.test(value);
    }

    @Override
    public boolean testWithPredicate(T value, Predicate<T> predicate) {
        return predicate.test(value);
    }

    @Override
    public Optional<R> mapIf(T value, Condition<T> condition, Function<T, R> mapper) {
        return condition.test(value) ? Optional.ofNullable(value).map(mapper) : Optional.empty();
    }

    @Override
    public Optional<R> mapIfWithPredicate(T value, Predicate<T> predicate, Function<T, R> mapper) {
        return predicate.test(value) ? Optional.ofNullable(value).map(mapper) : Optional.empty();
    }

    @Override
    public void acceptIf(T value, Condition<T> condition, Consumer<T> action) {
        if (condition.test(value)) {
            action.accept(value);
        }
    }

    @Override
    public void acceptIfWithPredicate(T value, Predicate<T> predicate, Consumer<T> action) {
        if (predicate.test(value)) {
            action.accept(value);
        }
    }

    @Override
    public R getIf(T value, Condition<T> condition, Supplier<R> supplier, R defaultValue) {
        return condition.test(value) ? supplier.get() : defaultValue;
    }

    @Override
    public R getIfWithPredicate(T value, Predicate<T> predicate, Supplier<R> supplier, R defaultValue) {
        return predicate.test(value) ? supplier.get() : defaultValue;
    }

    @Override
    public boolean testAll(T value, Condition<T>... conditions) {
        return Arrays.stream(conditions).allMatch(c -> c.test(value));
    }

    @Override
    public boolean testAllWithPredicate(T value, Predicate<T>... predicates) {
        return Arrays.stream(predicates).allMatch(p -> p.test(value));
    }

    @Override
    public boolean testAny(T value, Condition<T>... conditions) {
        return Arrays.stream(conditions).anyMatch(c -> c.test(value));
    }

    @Override
    public boolean testAnyWithPredicate(T value, Predicate<T>... predicates) {
        return Arrays.stream(predicates).anyMatch(p -> p.test(value));
    }

    @Override
    public Validator<T> validator(T value) {
        return new SimpleValidator<>(value);
    }

    @Override
    public ConditionChain<T, R> chain(T value) {
        return new SimpleConditionChain<>(value);
    }
    /**
     * 简单验证器实现 - 已修复重载冲突
     */
    private static class SimpleValidator<X> implements Validator<X> {

        private final X value;

        private final List<String> errors = new ArrayList<>();

        SimpleValidator(X value) {
            this.value = value;
        }

        @Override
        public Validator<X> validateWith(Condition<X> condition, String errorMessage) {
            if (!condition.test(value)) {
                errors.add(errorMessage);
            }
            return this;
        }

        @Override
        public Validator<X> validateWithPredicate(Predicate<X> predicate, String errorMessage) {
            if (!predicate.test(value)) {
                errors.add(errorMessage);
            }
            return this;
        }

        @Override
        public boolean isValid() {
            return errors.isEmpty();
        }

        @Override
        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        @Override
        public X getValidatedValue() throws ValidationException {
            if (!isValid()) {
                throw new ValidationException(errors);
            }
            return value;
        }
    }

    /**
     * 简单条件链实现
     */
    private class SimpleConditionChain<X, Y> implements ConditionChain<X, Y> {
        private final X value;
        private boolean conditionMet = true;

        @SuppressWarnings("unchecked")
        SimpleConditionChain(X value) {
            this.value = value;
        }

        @Override
        public ConditionChain<X, Y> when(Condition<X> condition) {
            if (conditionMet) {
                conditionMet = condition.test(value);
            }
            return this;
        }

        @Override
        public ConditionChain<X, Y> when(Predicate<X> predicate) {
            if (conditionMet) {
                conditionMet = predicate.test(value);
            }
            return this;
        }

        @Override
        public ConditionChain<X, Y> then(Consumer<X> action) {
            if (conditionMet && value != null) {
                action.accept(value);
            }
            return this;
        }

        @Override
        public Optional<Y> thenMap(Function<X, Y> mapper) {
            if (conditionMet && value != null) {
                return Optional.ofNullable(mapper.apply(value));
            }
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Y orElse(Y defaultValue) {
            return thenMap(v -> (Y) v).orElse(defaultValue);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Y orElseGet(Supplier<Y> supplier) {
            return thenMap(v -> (Y) v).orElseGet(supplier);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E extends Throwable> Y orElseThrow(Supplier<E> exceptionSupplier) throws E {
            return thenMap(v -> (Y) v).orElseThrow(exceptionSupplier);
        }
    }
}