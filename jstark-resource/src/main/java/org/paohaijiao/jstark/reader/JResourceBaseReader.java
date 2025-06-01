package org.paohaijiao.jstark.reader;

import java.lang.reflect.Field;

public class JResourceBaseReader {
    public static <T> T extractByPrefix(Object config, String prefix, Class<T> targetClass) {
        try {
            String[] parts = prefix.split("\\.");
            Object current = config;
            for (String part : parts) {
                Field field = current.getClass().getDeclaredField(part);
                field.setAccessible(true);
                current = field.get(current);
                if (current == null) {
                    return null;
                }
            }
            return targetClass.cast(current);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract config with prefix: " + prefix, e);
        }
    }
}
