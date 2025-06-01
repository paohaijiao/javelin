package com.paohaijiao.javelin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置ObjectMapper
        objectMapper.registerModule(new JavaTimeModule()); // 支持Java8日期时间API
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 日期不写为时间戳
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // 空对象不报错
    }

    /**
     * 复制源对象属性到目标对象
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <T>    源对象类型
     * @param <K>    目标对象类型
     */
    public static <T, K> void copyProperties(T source, K target) {
        if (source == null || target == null) {
            return;
        }
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        List<Field> sourceFields = Arrays.stream(sourceClass.getDeclaredFields())
                .collect(Collectors.toList());
        List<Field> targetFields = Arrays.stream(targetClass.getDeclaredFields())
                .collect(Collectors.toList());
        for (Field sourceField : sourceFields) {
            try {
                for (Field targetField : targetFields) {
                    if (sourceField.getName().equals(targetField.getName())) {
                        if (sourceField.getType().equals(targetField.getType())) {
                            sourceField.setAccessible(true);
                            targetField.setAccessible(true);
                            Object value = sourceField.get(source);
                            targetField.set(target, value);
                        }
                        break;
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to copy property: " + sourceField.getName(), e);
            }
        }
    }

    /**
     * 复制源对象List到目标Class的List
     *
     * @param sourceList  源对象List
     * @param targetClass 目标Class
     * @param <T>         源对象类型
     * @param <K>         目标对象类型
     * @return 目标对象List
     */
    public static <T, K> List<K> copyList(List<T> sourceList, Class<K> targetClass) {
        if (sourceList == null) {
            return null;
        }
        List<K> targetList = new ArrayList<>(sourceList.size());
        for (T source : sourceList) {
            if (source == null) {
                targetList.add(null);
                continue;
            }
            try {
                K target = targetClass.getDeclaredConstructor().newInstance();
                copyProperties(source, target);
                targetList.add(target);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to create instance of " + targetClass.getName(), e);
            }
        }
        return targetList;
    }

    /**
     * 将 Map 转换为 Java Bean
     *
     * @param map       源 Map
     * @param beanClass 目标 Bean 的 Class
     * @param <T>       Bean 类型
     * @return 转换后的 Bean 对象
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        if (map == null || beanClass == null) {
            return null;
        }
        try {
            T bean = beanClass.getDeclaredConstructor().newInstance();
            List<Field> fields = getAllFields(beanClass);
            for (Field field : fields) {
                String fieldName = field.getName();
                String mapKey = findMapKeyIgnoreCase(map, fieldName);
                if (mapKey != null && map.get(mapKey) != null) {
                    try {
                        field.setAccessible(true);
                        Object value = map.get(mapKey);
                        if (field.getType().isAssignableFrom(value.getClass())) {
                            field.set(bean, value);
                        } else {
                            Object convertedValue = convertValue(value, field.getType());
                            field.set(bean, convertedValue);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to set field: " + fieldName, e);
                    }
                }
            }

            return bean;
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to create bean instance", e);
        }
    }

    /**
     * 将 Java Bean 转换为 Map
     *
     * @param bean 源 Bean
     * @return 转换后的 Map
     */
    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        List<Field> fields = getAllFields(bean.getClass());

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(bean);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to get field value: " + field.getName(), e);
            }
        }
        return map;
    }

    /**
     * 获取类及其父类的所有字段
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 忽略大小写查找 Map 中的 key
     */
    private static String findMapKeyIgnoreCase(Map<String, Object> map, String key) {
        for (String mapKey : map.keySet()) {
            if (mapKey.equalsIgnoreCase(key)) {
                return mapKey;
            }
        }
        return null;
    }

    /**
     * 简单类型转换
     */
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (value instanceof String && Number.class.isAssignableFrom(targetType)) {
            String strValue = (String) value;
            try {
                if (targetType == Integer.class || targetType == int.class) {
                    return Integer.parseInt(strValue);
                } else if (targetType == Long.class || targetType == long.class) {
                    return Long.parseLong(strValue);
                } else if (targetType == Double.class || targetType == double.class) {
                    return Double.parseDouble(strValue);
                } else if (targetType == Float.class || targetType == float.class) {
                    return Float.parseFloat(strValue);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Failed to convert string to number: " + strValue, e);
            }
        }
        if (value instanceof Number && targetType == String.class) {
            return value.toString();
        }
        throw new RuntimeException("Unsupported type conversion from " +
                value.getClass().getName() + " to " + targetType.getName());
    }

    /**
     * 将JSON字符串转换为Java对象
     *
     * @param json  JSON字符串
     * @param clazz 目标Java类
     * @param <T>   泛型类型
     * @return 转换后的Java对象
     * @throws RuntimeException 如果转换失败
     */
    public static <T> T jsonToBean(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to Bean: " + e.getMessage(), e);
        }
    }

    /**
     * 将Java对象转换为JSON字符串
     *
     * @param bean Java对象
     * @return JSON字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String beanToJson(Object bean) {
        if (bean == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Bean to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * 将Java对象转换为格式化的JSON字符串（美化输出）
     *
     * @param bean Java对象
     * @return 格式化的JSON字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String beanToPrettyJson(Object bean) {
        if (bean == null) {
            return null;
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Bean to pretty JSON: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的List
     *
     * @param json         JSON字符串
     * @param elementClass List元素类型
     * @param <T>          泛型类型
     * @return 转换后的List
     * @throws RuntimeException 如果转换失败
     */
    public static <T> List<T> jsonToList(String json, Class<T> elementClass) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to List: " + e.getMessage(), e);
        }
    }

    /**
     * 将JSON字符串转换为指定类型的Map
     *
     * @param json       JSON字符串
     * @param keyClass   Map的key类型
     * @param valueClass Map的value类型
     * @param <K>        key泛型类型
     * @param <V>        value泛型类型
     * @return 转换后的Map
     * @throws RuntimeException 如果转换失败
     */
    public static <K, V> Map<K, V> jsonToMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to Map: " + e.getMessage(), e);
        }
    }
    /**
     * 将 JSON 字符串转换为 Map
     * @param json JSON 字符串
     * @return 转换后的 Map 对象
     * @throws RuntimeException 如果转换失败
     */
    public static Map<String, Object> jsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to Map: " + e.getMessage(), e);
        }
    }

    /**
     * 将 Map 转换为 JSON 字符串
     * @param map 要转换的 Map
     * @return 转换后的 JSON 字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String mapToJson(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Map to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * 将 Map 转换为格式化的 JSON 字符串（美化输出）
     * @param map 要转换的 Map
     * @return 格式化的 JSON 字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String mapToPrettyJson(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Map to pretty JSON: " + e.getMessage(), e);
        }
    }
}


