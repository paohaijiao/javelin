package com.github.paohaijiao.condition;

import java.util.List;
import java.util.function.Predicate;

/**
 * 验证器接口
 */
interface Validator<T> {

    Validator<T> validate(Condition<T> condition, String errorMessage);

    Validator<T> validate(Predicate<T> predicate, String errorMessage);

    boolean isValid();

    List<String> getErrors();

    T getValidatedValue() throws ValidationException;
}
