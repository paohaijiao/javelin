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
package com.github.paohaijiao.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

/**
 * 通用 Bean 拷贝工具类（纯 JDK 反射实现，不依赖任何第三方）
 *
 * @author YourName
 */
public class JQuickBeanCopyUtils {

    /**
     * 将源对象的属性值复制到目标对象（浅拷贝）
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copy(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        copyProperties(source, target, null, true);
    }

    /**
     * 将源对象的属性值复制到目标对象，并支持忽略指定字段
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略的属性名列表
     */
    public static void copy(Object source, Object target, String... ignoreProperties) {
        if (source == null || target == null) {
            return;
        }
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreProperties));
        copyProperties(source, target, ignoreSet, true);
    }

    /**
     * 根据源对象和目标 Class，创建一个新的目标对象并拷贝属性
     *
     * @param source     源对象
     * @param targetType 目标类型
     * @param <T>        目标类型
     * @return 新的目标对象，源为 null 则返回 null
     */
    public static <T> T copy(Object source, Class<T> targetType) {
        return copyToNew(source, targetType, null);
    }

    /**
     * 根据源对象和目标 Class，创建新对象，并支持忽略指定属性
     *
     * @param source           源对象
     * @param targetType       目标类型
     * @param ignoreProperties 忽略的属性名列表
     * @param <T>              目标类型
     * @return 新的目标对象
     */
    public static <T> T copy(Object source, Class<T> targetType, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreProperties));
        return copyToNew(source, targetType, ignoreSet);
    }

    /**
     * 核心方法：通过反射创建新对象并拷贝属性
     */
    private static <T> T copyToNew(Object source, Class<T> targetType, Set<String> ignoreProperties) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetType.getDeclaredConstructor().newInstance();
            copyProperties(source, target, ignoreProperties, true);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("创建 " + targetType.getName() + " 实例失败", e);
        }
    }

    /**
     * 将源集合中的每个元素转换为目标类型的元素，生成新的 List
     *
     * @param sourceList 源集合
     * @param targetType 目标元素类型
     * @param <T>        目标类型
     * @return 转换后的 List（不会返回 null）
     */
    public static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetType) {
        return copyToList(sourceList, targetType, (Set<String>) null);
    }

    /**
     * 将源集合转换为目标类型的 List，并支持忽略指定字段
     *
     * @param sourceList       源集合
     * @param targetType       目标元素类型
     * @param ignoreProperties 忽略的属性名列表
     * @param <T>              目标类型
     * @return 转换后的 List
     */
    public static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetType, String... ignoreProperties) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreProperties));
        return copyToList(sourceList, targetType, ignoreSet);
    }

    /**
     * 更灵活的 List 拷贝，支持自定义对象创建工厂
     *
     * @param sourceList    源集合
     * @param targetFactory 目标实例创建工厂
     * @param <S>           源类型
     * @param <T>           目标类型
     * @return 转换后的 List
     */
    public static <S, T> List<T> copyToList(Collection<S> sourceList, Supplier<T> targetFactory) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<T> resultList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            if (source == null) {
                resultList.add(null);
                continue;
            }
            T target = targetFactory.get();
            copyProperties(source, target, null, true);
            resultList.add(target);
        }
        return resultList;
    }

    private static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetType, Set<String> ignoreProperties) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<T> resultList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            if (source == null) {
                resultList.add(null);
                continue;
            }
            resultList.add(copyToNew(source, targetType, ignoreProperties));
        }
        return resultList;
    }

    /**
     * 合并属性：将源对象的非 null 属性覆盖到目标对象
     */
    public static void mergeNonNull(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        copyProperties(source, target, null, false);
    }


    /**
     * 核心拷贝逻辑：遍历源对象的所有字段，赋值给目标对象的同名字段
     *
     * @param source         源对象
     * @param target         目标对象
     * @param ignoreSet      忽略的字段名集合（可为 null）
     * @param copyNullValues 是否拷贝 null 值（true=拷贝，false=跳过 null）
     */
    private static void copyProperties(Object source, Object target, Set<String> ignoreSet, boolean copyNullValues) {
        if (source == null || target == null) {
            return;
        }
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        Map<String, Field> targetFieldMap = getAllFields(targetClass);
        for (Field sourceField : getAllFields(sourceClass).values()) {
            String fieldName = sourceField.getName();
            if (Modifier.isStatic(sourceField.getModifiers()) || Modifier.isFinal(sourceField.getModifiers())) { // 忽略 static、final 字段
                continue;
            }
            if (ignoreSet != null && ignoreSet.contains(fieldName)) {// 检查是否需要忽略
                continue;
            }

            Field targetField = targetFieldMap.get(fieldName);
            if (targetField == null) {
                continue;
            }
            if (Modifier.isStatic(targetField.getModifiers()) || Modifier.isFinal(targetField.getModifiers())) { // 忽略目标字段的 static、final
                continue;
            }
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                Object value = sourceField.get(source);
                if (!copyNullValues && value == null) {
                    continue;
                }
                if (value != null && !targetField.getType().isAssignableFrom(value.getClass())) {
                    value = convertIfNeeded(value, targetField.getType());
                }
                targetField.set(target, value);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    /**
     * 获取类的所有字段（包括父类，不包含 Object）
     */
    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!fieldMap.containsKey(field.getName())) {
                    fieldMap.put(field.getName(), field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fieldMap;
    }

    /**
     * 简单的类型转换（主要处理基本类型和包装类之间的转换）
     */
    private static Object convertIfNeeded(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        Class<?> sourceType = value.getClass();
        if (targetType.isPrimitive() || sourceType.isPrimitive()) {// 基本类型和包装类兼容
            if (targetType == Integer.class || targetType == int.class) {            // 这里简化处理，实际可根据需要扩展
                return ((Number) value).intValue();
            }
            if (targetType == Long.class || targetType == long.class) {
                return ((Number) value).longValue();
            }
            if (targetType == Double.class || targetType == double.class) {
                return ((Number) value).doubleValue();
            }
            if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.valueOf(value.toString());
            }
        }
        return value;
    }
}