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
package com.paohaijiao.javelin.exception;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;

public interface JAssert {

    public static void throwNewException( String message) {
        throw new IllegalArgumentException(message);
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
    static void isNull( Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    static void isNull( Object object, String message, Object... args) {
        if (object != null) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    static void notNull( Object object, String message, Object... args) {
        if (object == null) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    static void isTrue(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    static void isFalse(boolean expression, String message, Object... args) {
        if (expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    static void notEmptyStr(String value, String message) {
        if (StrUtil.isEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    static void notEmptyStr(String value, String message, Object... args) {
        if (StrUtil.isEmpty(value)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    static void notEmptyCol(Collection<?> value, String message) {
        if (CollUtil.isEmpty(value) || value.size() < 1) {
            throw new IllegalArgumentException(message);
        }
    }

    static void notEmptyCol(Collection<?> value, String message, Object... args) {
        if (CollUtil.isEmpty(value) || value.size() < 1) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    static void notEmptyArray(Object[] value, String message) {
        if (ArrayUtil.isEmpty(value) || value.length < 1) {
            throw new IllegalArgumentException(message);
        }
    }

    static void notEmptyArray(Object[] value, String message, Object... args) {
        if (ArrayUtil.isEmpty(value) || value.length < 1) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }
}
