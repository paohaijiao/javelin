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
package com.github.paohaijiao.reader.impl;

import com.github.paohaijiao.reader.JResourceBaseReader;
import com.github.paohaijiao.reader.JResourceReader;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public class JResourcePropertiesReader<T> extends JResourceBaseReader implements JResourceReader<T> {
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

    @Override
    public T parse(InputStream inputStream, Class<T> targetClass, String key) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();
            String prefix = key.endsWith(".") ? key : key + ".";
            Properties filteredProps = new Properties();
            properties.stringPropertyNames().stream()
                    .filter(propKey -> propKey.startsWith(prefix))
                    .forEach(propKey -> {
                        String newKey = propKey.substring(prefix.length());
                        filteredProps.setProperty(newKey, properties.getProperty(propKey));
                    });
            populateObject(instance, filteredProps);
            return instance;
        } catch (Exception e) {
            throw new IOException("Failed to create or populate instance of " + targetClass.getName(), e);
        }
    }

    private <T> void populateObject(T instance, Properties properties) throws Exception {
        for (String propKey : properties.stringPropertyNames()) {
            String[] parts = propKey.split("\\.");
            Object current = instance;
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                PropertyDescriptor pd = new PropertyDescriptor(part, current.getClass());
                Method getter = pd.getReadMethod();
                Object nested = getter.invoke(current);
                if (nested == null) {
                    Method setter = pd.getWriteMethod();
                    nested = pd.getPropertyType().getDeclaredConstructor().newInstance();
                    setter.invoke(current, nested);
                }
                current = nested;
            }
            String finalPart = parts[parts.length - 1];
            PropertyDescriptor finalPd = new PropertyDescriptor(finalPart, current.getClass());
            Method setter = finalPd.getWriteMethod();
            if (setter != null) {
                Class<?> paramType = setter.getParameterTypes()[0];
                String value = properties.getProperty(propKey);
                Object convertedValue = convertValue(value, paramType);
                setter.invoke(current, convertedValue);
            }
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
