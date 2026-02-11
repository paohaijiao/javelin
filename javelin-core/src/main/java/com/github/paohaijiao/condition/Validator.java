package com.github.paohaijiao.condition;

import java.util.List;
import java.util.function.Predicate;

/**
 * 验证器接口
 */
public interface Validator<T> {

    /**
     * 使用Condition进行验证
     */
    Validator<T> validateWith(Condition<T> condition, String errorMessage);

    /**
     * 使用Predicate进行验证
     */
    Validator<T> validateWithPredicate(Predicate<T> predicate, String errorMessage);

    /**
     * 是否所有验证都通过
     */
    boolean isValid();

    /**
     * 获取所有错误信息
     */
    List<String> getErrors();

    /**
     * 获取验证通过的值，如果验证失败则抛出异常
     */
    T getValidatedValue() throws ValidationException;
}
