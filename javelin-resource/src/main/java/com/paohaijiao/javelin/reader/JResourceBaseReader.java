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
package com.paohaijiao.javelin.reader;

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
