package org.paohaijiao.jstark.reader.impl;

import org.paohaijiao.jstark.reader.JResourceReader;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class JResourcePropertiesReader<T> implements JResourceReader<T> {
    @Override
    public T parse(InputStream inputStream, Class<T> targetClass) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();
            properties.stringPropertyNames().forEach(fullKey -> {
                try {
                    String[] parts = fullKey.split("\\.");
                    Object currentObj = instance;
                    for (int i = 0; i < parts.length - 1; i++) {
                        String part = parts[i];
                        PropertyDescriptor pd = new PropertyDescriptor(part, currentObj.getClass());
                        Method getter = pd.getReadMethod();
                        Object nestedObj = getter.invoke(currentObj);
                        if (nestedObj == null) {
                            Method setter = pd.getWriteMethod();
                            nestedObj = pd.getPropertyType().getDeclaredConstructor().newInstance();
                            setter.invoke(currentObj, nestedObj);
                        }
                        currentObj = nestedObj;
                    }
                    String finalPart = parts[parts.length - 1];
                    PropertyDescriptor finalPd = new PropertyDescriptor(finalPart, currentObj.getClass());
                    Method setter = finalPd.getWriteMethod();
                    if (setter != null) {
                        Class<?> paramType = setter.getParameterTypes()[0];
                        String value = properties.getProperty(fullKey);
                        Object convertedValue = convertValue(value, paramType);
                        setter.invoke(currentObj, convertedValue);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to set property: " + fullKey, e);
                }
            });

            return instance;
        } catch (Exception e) {
            throw new IOException("Failed to create instance of " + targetClass.getName(), e);
        }
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else {
            throw new IllegalArgumentException("Unsupported property type: " + targetType);
        }
    }
}
