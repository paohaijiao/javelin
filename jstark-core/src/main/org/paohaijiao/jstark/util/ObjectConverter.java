package org.paohaijiao.jstark.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectConverter {
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

        // 如果目标已经是所需的List类型
        if (target instanceof List<?>) {
            List<?> list = (List<?>) target;
            // 检查List中的元素类型是否匹配
            if (!list.isEmpty() && !clazz.isInstance(list.get(0))) {
                throw new IllegalArgumentException("List contains elements of wrong type. Expected: " +
                        clazz.getName() + ", found: " + list.get(0).getClass().getName());
            }
            @SuppressWarnings("unchecked")
            List<T> result = (List<T>) list;
            return result;
        }

        // 处理数组情况
        if (target.getClass().isArray()) {
            return Arrays.stream((Object[]) target)
                    .map(clazz::cast)
                    .collect(Collectors.toList());
        }

        // 处理其他集合类型
        if (target instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) target;
            return collection.stream()
                    .map(clazz::cast)
                    .collect(Collectors.toList());
        }

        // 处理单个对象情况
        if (clazz.isInstance(target)) {
            return Collections.singletonList(clazz.cast(target));
        }

        // 尝试JSON转换（如果有Jackson/Gson等库）
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
        // 如果有Jackson库
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (target instanceof String) {
                // 如果是JSON字符串
                return mapper.readValue((String) target,
                        mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            } else {
                // 如果是对象，先转为JSON字符串再转换
                String json = mapper.writeValueAsString(target);
                return mapper.readValue(json,
                        mapper.getTypeFactory().constructCollectionType(List.class, clazz));
            }
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion failed", e);
        }

        // 可以添加Gson等其他JSON库的支持
    }
}
