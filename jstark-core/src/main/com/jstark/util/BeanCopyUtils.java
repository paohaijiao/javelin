package com.jstark.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BeanCopyUtils {
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

}
