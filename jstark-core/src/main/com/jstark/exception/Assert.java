package com.jstark.exception;

import java.util.Collection;

public interface Assert {
    /**
     * 断言对象不为 null
     * @param object 要检查的对象
     * @param message 异常消息
     * @throws IllegalArgumentException 如果对象为 null
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    /**
     * 断言对象不为 null
     * @param object 要检查的对象
     * @param message 异常消息
     * @throws IllegalArgumentException 如果对象为 null
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }
    /**
     * 断言表达式为 true
     * @param expression 布尔表达式
     * @param message 异常消息
     * @throws IllegalArgumentException 如果表达式为 false
            */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
    /**
     * 断言表达式为 true
     * @param expression 布尔表达式
     * @param message 异常消息
     * @throws IllegalArgumentException 如果表达式为 false
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串不为空（不为 null 且长度大于 0）
     * @param text 要检查的字符串
     * @param message 异常消息
     * @throws IllegalArgumentException 如果字符串为空
     */
    public static void hasText(String text, String message) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言集合不为空（不为 null 且至少包含一个元素）
     * @param collection 要检查的集合
     * @param message 异常消息
     * @throws IllegalArgumentException 如果集合为空
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象是特定类型的实例
     * @param type 期望的类型
     * @param obj 要检查的对象
     * @param message 异常消息
     * @throws IllegalArgumentException 如果对象不是指定类型
     */
    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(message);
        }
    }
}
