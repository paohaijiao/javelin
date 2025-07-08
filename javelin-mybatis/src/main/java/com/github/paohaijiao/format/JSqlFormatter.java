package com.github.paohaijiao.format;

import com.github.paohaijiao.map.JMultiValuedMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSqlFormatter {


    public static JMultiValuedMap<String, String> parsePlaceholders(String sqlTemplate) {
        JMultiValuedMap<String, String> placeholderMap = new JMultiValuedMap<>();
        Pattern pattern = Pattern.compile("#\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(sqlTemplate);
        while (matcher.find()) {
            String placeholder = matcher.group(1); //FieldName
            placeholderMap.put(placeholder, matcher.group());
        }
        return placeholderMap;
    }

    public static JMultiValuedMap<String, Object> getFieldValues(Object entity, Iterable<String> fieldNames) {
        JMultiValuedMap<String, Object> fieldValueMap = new JMultiValuedMap<>();
        Class<?> clazz = entity.getClass();
        try {
            for (String fieldName : fieldNames) {
                Field field;
                try {
                    field = clazz.getDeclaredField(fieldName);//get Value By FieldId
                } catch (NoSuchFieldException e) {
                    try {
                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        Object value = clazz.getMethod(getterName).invoke(entity);
                        fieldValueMap.put(fieldName, value);
                        continue;
                    } catch (Exception ex) {
                        throw new RuntimeException("unable to get field " + fieldName + "'s value", ex);
                    }
                }
                field.setAccessible(true);
                Object value = field.get(entity);
                fieldValueMap.put(fieldName, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fieldValueMap;
    }
    public static String replacePlaceholders(String sqlTemplate, JMultiValuedMap<String, Object> fieldValueMap) {
        String result = sqlTemplate;
        for (Map.Entry<String, Object> entry : fieldValueMap.entrySet()) {
            String placeholder = "#{" + entry.getKey() + "}";
            Object value = entry.getValue();
            String replacement;
            if (value == null) {
                replacement = "NULL";
            } else if (value instanceof String || value instanceof Character) {
                replacement = "'" + value.toString().replace("'", "''") + "'"; // 转义单引号
            } else if (value instanceof Number) {
                replacement = value.toString();
            } else if (value instanceof Boolean) {
                replacement = ((Boolean) value) ? "1" : "0";
            } else {
                replacement = "'" + value.toString().replace("'", "''") + "'";
            }
            result = result.replace(placeholder, replacement);
        }
        return result;
    }
}
