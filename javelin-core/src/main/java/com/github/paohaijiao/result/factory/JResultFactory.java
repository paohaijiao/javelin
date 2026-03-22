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
package com.github.paohaijiao.result.factory;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.paohaijiao.result.JResult;
import com.github.paohaijiao.result.JResultConverter;
import com.github.paohaijiao.result.impl.*;
import com.github.paohaijiao.type.JTypeReference;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.result.factory
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/3/22
 */
public class JResultFactory {

    private static final Map<Class<?>, JResultConverter<?>> converters = new HashMap<>();

    static {
        converters.put(String.class, new JStringResponseConverter());
        converters.put(JResult.class, new JPassthroughResponseConverter());
        converters.put(Map.class, new JMapResponseConverter());
        converters.put(JSONObject.class, new JSonObjectResponseConverter());
        converters.put(Object.class, new JObjectResponseConverter());
        converters.put(byte[].class, new JByteResponseConverter());
    }

    @SuppressWarnings("unchecked")
    public static <T> T createResult(JResult result, Class<T> targetType) throws IOException {
        JResultConverter<T> converter = (JResultConverter<T>) converters.get(targetType);
        if (converter == null) {
            if (result == null) {
                return null;
            }
            if (isPrimitiveOrWrapper(targetType)) {
                return convertPrimitive(result.getString(), targetType);
            }
            return JSONUtil.toBean(result.getString(), targetType);
        }
        return converter.convert(result);
    }

    /**
     * Convert response to generic type
     */
    @SuppressWarnings("unchecked")
    public static <T> T createResult(JResult response, JTypeReference<T> typeReference) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("response cannot be null");
        }
        if (typeReference == null) {
            throw new IllegalArgumentException("type reference cannot be null");
        }
        Type type = typeReference.getType();
        if (type instanceof Class) {
            return createResult(response, (Class<T>) type);
        }
        if (type == byte.class || type == Byte.TYPE) {
            return createResult(response, (Class<T>) type);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            JResultGenericConverter<T> converter = new JResultGenericConverter<>(rawType);
            return converter.convert(response);
        }
        throw new IllegalArgumentException("unsupported type: " + type);
    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Double.class ||
                clazz == Float.class ||
                clazz == Boolean.class ||
                clazz == Byte.class ||
                clazz == Character.class ||
                clazz == Short.class;
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertPrimitive(String value, Class<T> targetType) {
        if (targetType == String.class) {
            return (T) value;
        }
        if (targetType == Integer.class || targetType == int.class) {
            return (T) Integer.valueOf(value);
        }
        if (targetType == Long.class || targetType == long.class) {
            return (T) Long.valueOf(value);
        }
        if (targetType == Double.class || targetType == double.class) {
            return (T) Double.valueOf(value);
        }
        if (targetType == Float.class || targetType == float.class) {
            return (T) Float.valueOf(value);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return (T) Boolean.valueOf(value);
        }
        if (targetType == Byte.class || targetType == byte.class) {
            return (T) Byte.valueOf(value);
        }
        if (targetType == Character.class || targetType == char.class) {
            if (value.length() > 0) {
                return (T) Character.valueOf(value.charAt(0));
            }
            return null;
        }
        if (targetType == Short.class || targetType == short.class) {
            return (T) Short.valueOf(value);
        }
        return null;
    }

    /**
     * Register custom converter
     */
    public static <T> void registerConverter(Class<T> targetType, JResultConverter<T> converter) {
        converters.put(targetType, converter);
    }
}
