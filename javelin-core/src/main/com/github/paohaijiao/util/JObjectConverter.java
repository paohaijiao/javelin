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
package com.github.paohaijiao.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public class JObjectConverter {
    public static <T> T assign(Object target, Class<T> clazz) {
        if (target == null) {
            return null;
        }
        if (clazz.isInstance(target)) {
            return clazz.cast(target);
        }
        throw new ClassCastException("Cannot assign object of type "
                + target.getClass().getName() + " to " + clazz.getName());
    }
    /**
     * 将目标对象转换为指定类型的List
     * @param target 目标对象，可以是单个对象、数组、集合等
     * @param clazz 目标元素类型
     * @param <T> 泛型类型
     * @return 转换后的List，如果无法转换则返回空List
     * @throws IllegalArgumentException 如果目标对象无法转换为指定类型
     */
    public static <T> List<T> assignable(Object target, Class<T> clazz) {
        if (target == null) {
            return Collections.emptyList();
        }

        if (target instanceof List<?>) {
            List<?> list = (List<?>) target;
            if (!list.isEmpty() && !clazz.isInstance(list.get(0))) {
                throw new IllegalArgumentException("List contains elements of wrong type. Expected: " +
                        clazz.getName() + ", found: " + list.get(0).getClass().getName());
            }
            @SuppressWarnings("unchecked")
            List<T> result = (List<T>) list;
            return result;
        }
        if (target.getClass().isArray()) {
            return Arrays.stream((Object[]) target)
                    .map(clazz::cast)
                    .collect(Collectors.toList());
        }
        if (target instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) target;
            return collection.stream()
                    .map(clazz::cast)
                    .collect(Collectors.toList());
        }
        if (clazz.isInstance(target)) {
            return Collections.singletonList(clazz.cast(target));
        }
        try {
            return attemptJsonConversion(target, clazz);
        } catch (Exception e) {
            // 忽略JSON转换失败
        }

        throw new IllegalArgumentException("Cannot convert object of type " +
                target.getClass().getName() + " to List<" + clazz.getName() + ">");
    }

    /**
     * 尝试使用JSON库进行转换（可选）
     */
    private static <T> List<T> attemptJsonConversion(Object target, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (target instanceof String) {
                return mapper.readValue((String) target,
                        mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            } else {
                String json = mapper.writeValueAsString(target);
                return mapper.readValue(json,
                        mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            }
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion failed", e);
        }
    }

    /**
     *  convert Object List To Map List
     * @param list
     * @return
     */
    public static List<Map<String, Object>> convert(List<?> list) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Map) {
                mapList.add((Map<String, Object>) object);
            }else if (object instanceof Object) {
                Map<String, Object> fieldAndValue= JReflectionUtils.getFieldAndFieldValueByObject(object);
                mapList.add(fieldAndValue);
            }
        }
        return mapList;
    }
}
