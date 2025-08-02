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
package com.github.paohaijiao.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * packageName com.github.paohaijiao.type
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/3
 */
public class JGenericlTypeConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();

    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = new HashMap<>();

    static {
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER.put(void.class, Void.class);
        for (Map.Entry<Class<?>, Class<?>> entry : PRIMITIVE_TO_WRAPPER.entrySet()) {
            WRAPPER_TO_PRIMITIVE.put(entry.getValue(), entry.getKey());
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, Class<T> targetType) throws IOException {
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return (T) value;
        }

        if (isPrimitiveOrWrapper(targetType)) {
            return convertPrimitive(value, targetType);
        }
        if (targetType == String.class) {
            return (T) value.toString();
        }
        if (targetType == byte[].class && value instanceof String) {
            return (T) ((String) value).getBytes();
        }
        if (Collection.class.isAssignableFrom(targetType)) {
            return convertToCollection(value, targetType, null);
        }
        if (Map.class.isAssignableFrom(targetType)) {
            return convertToMap(value, targetType, null, null);
        }
        if (targetType.isArray()) {
            return convertToArray(value, targetType.getComponentType());
        }

        if (value instanceof String) {
            try {
                return objectMapper.readValue((String) value, targetType);
            } catch (IOException e) {
                if (targetType == String.class) {
                    return (T) value;
                }
                throw e;
            }
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("cannot convert " + value.getClass() + " to " + targetType, e);
        }
    }
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, TypeReference<T> typeReference) throws IOException {
        if (value == null || typeReference == null) {
            return null;
        }
        if (value instanceof String) {
            String json = (String) value;
            return objectMapper.readValue(json, typeReference);
        }
        return objectMapper.convertValue(value, typeReference);
    }
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, Type type) throws IOException {
        if (value == null) {
            return null;
        }
        if (type instanceof Class && ((Class<?>) type).isPrimitive()) {
            return convertPrimitive(value, (Class<T>) type);
        }
        if (type instanceof Class && isWrapperType((Class<?>) type)) {
            Class<?> primitiveType = WRAPPER_TO_PRIMITIVE.get(type);
            if (primitiveType != null) {
                return convertPrimitive(value, (Class<T>) type);
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type rawType = pType.getRawType();
            if (rawType == List.class || rawType == Collection.class) {
                Type elementType = pType.getActualTypeArguments()[0];
                return convertToCollection(value, (Class<T>) rawType, elementType);
            }
            if (rawType == Set.class) {
                Type elementType = pType.getActualTypeArguments()[0];
                return convertToCollection(value, (Class<T>) rawType, elementType);
            }
            if (rawType == Map.class) {
                Type keyType = pType.getActualTypeArguments()[0];
                Type valueType = pType.getActualTypeArguments()[1];
                return convertToMap(value, (Class<T>) rawType, keyType, valueType);
            }
        }
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        if (value instanceof String) {
            return objectMapper.readValue((String) value, javaType);
        }
        return objectMapper.convertValue(value, javaType);
    }
    private static <T> T convertPrimitive(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        String strValue = value.toString();
        if (targetType == boolean.class || targetType == Boolean.class) {
            return (T) Boolean.valueOf(strValue);
        }
        if (targetType == byte.class || targetType == Byte.class) {
            return (T) Byte.valueOf(strValue);
        }
        if (targetType == char.class || targetType == Character.class) {
            return (T) Character.valueOf(strValue.charAt(0));
        }
        if (targetType == short.class || targetType == Short.class) {
            return (T) Short.valueOf(strValue);
        }
        if (targetType == int.class || targetType == Integer.class) {
            return (T) Integer.valueOf(strValue);
        }
        if (targetType == long.class || targetType == Long.class) {
            return (T) Long.valueOf(strValue);
        }
        if (targetType == float.class || targetType == Float.class) {
            return (T) Float.valueOf(strValue);
        }
        if (targetType == double.class || targetType == Double.class) {
            return (T) Double.valueOf(strValue);
        }
        throw new IllegalArgumentException("unsupported primitive type: " + targetType);
    }
    @SuppressWarnings("unchecked")
    private static <T, E> T convertToCollection(Object value, Class<T> collectionType, Type elementType) throws IOException {
        if (value == null) {
            return null;
        }
        Collection<E> collection;
        if (collectionType == List.class || collectionType == Collection.class) {
            collection = new ArrayList<>();
        } else if (collectionType == Set.class) {
            collection = new HashSet<>();
        } else {
            throw new IllegalArgumentException("unsupported collection type: " + collectionType);
        }
        if (elementType == null || elementType == Object.class) {
            if (value instanceof Collection) {
                collection.addAll((Collection<? extends E>) value);
            } else if (value instanceof String) {
                List<?> list = objectMapper.readValue((String) value, List.class);
                collection.addAll((Collection<? extends E>) list);
            } else if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    collection.add((E) Array.get(value, i));
                }
            } else {
                collection.add((E) value);
            }
        } else {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(
                    collectionType,
                    objectMapper.getTypeFactory().constructType(elementType)
            );
            if (value instanceof String) {
                collection = objectMapper.readValue((String) value, javaType);
            } else {
                collection = objectMapper.convertValue(value, javaType);
            }
        }
        return (T) collection;
    }
    @SuppressWarnings("unchecked")
    private static <T, K, V> T convertToMap(Object value, Class<T> mapType, Type keyType, Type valueType) throws IOException {
        if (value == null) {
            return null;
        }
        if (keyType == null || valueType == null) {
            if (value instanceof String) {
                return (T) objectMapper.readValue((String) value, new TypeReference<Map<String, Object>>() {});
            }
            return (T) objectMapper.convertValue(value, Map.class);
        }
        JavaType javaType = objectMapper.getTypeFactory().constructMapType(
                Map.class,
                objectMapper.getTypeFactory().constructType(keyType),
                objectMapper.getTypeFactory().constructType(valueType)
        );
        if (value instanceof String) {
            return objectMapper.readValue((String) value, javaType);
        }
        return objectMapper.convertValue(value, javaType);
    }

    @SuppressWarnings("unchecked")
    private static <T, E> T convertToArray(Object value, Class<E> componentType) throws IOException {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            Object newArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(newArray, i, convert(Array.get(value, i), componentType));
            }
            return (T) newArray;
        }
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            Object array = Array.newInstance(componentType, collection.size());
            int i = 0;
            for (Object item : collection) {
                Array.set(array, i++, convert(item, componentType));
            }
            return (T) array;
        }

        if (value instanceof String) {
            if (componentType == String.class) {
                return (T) objectMapper.readValue((String) value, String[].class);
            }else if (componentType == short.class) {
                return (T) objectMapper.readValue((String) value, short[].class);
            }else if(componentType == Short.class ){
                return (T) objectMapper.readValue((String) value, Short[].class);
            } else if (componentType == int.class) {
                return (T) objectMapper.readValue((String) value, int[].class);
            }else if(componentType == Integer.class ){
                return (T) objectMapper.readValue((String) value, Integer[].class);
            }else if (componentType == long.class) {
                return (T) objectMapper.readValue((String) value, long[].class);
            }else if(componentType == Long.class ){
                return (T) objectMapper.readValue((String) value, Long[].class);
            }else if (componentType == float.class) {
                return (T) objectMapper.readValue((String) value, float[].class);
            }else if(componentType == Float.class ){
                return (T) objectMapper.readValue((String) value, Float[].class);
            }else if ( componentType == double.class) {
                return (T) objectMapper.readValue((String) value, double[].class);
            }else if(componentType == Double.class ){
                return (T) objectMapper.readValue((String) value, Double[].class);
            }else if ( componentType == boolean.class) {
                return (T) objectMapper.readValue((String) value, boolean[].class);
            }else if(componentType == Boolean.class ){
                return (T) objectMapper.readValue((String) value, Boolean[].class);
            }else {
                JavaType javaType = objectMapper.getTypeFactory().constructArrayType(componentType);
                return objectMapper.readValue((String) value, javaType);
            }
        }
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, convert(value, componentType));
        return (T) array;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || isWrapperType(type);
    }

    private static boolean isWrapperType(Class<?> type) {
        return WRAPPER_TO_PRIMITIVE.containsKey(type);
    }
}
