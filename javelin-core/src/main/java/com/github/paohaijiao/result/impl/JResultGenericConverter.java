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
package com.github.paohaijiao.result.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paohaijiao.result.JResultConverter;
import com.github.paohaijiao.result.JResult;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * packageName com.github.paohaijiao.result
 *
 * @author Martin
 * @version 1.0.0
 * @className JResultGenericConverter
 * @date 2025/6/28
 * @description
 */
public class JResultGenericConverter<T> implements JResultConverter<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Type resultType;

    public JResultGenericConverter(Type resultType) {
        this.resultType = resultType;
    }

    @Override
    public T  convert(JResult result) throws IOException {
        if (result == null) {
            return null;
        }
        if (resultType instanceof Class) {
            Class<T> targetClass = (Class<T>) resultType;
            if (targetClass.isInstance(result.getString())) {
                return (T) result.getString();
            }
            if (targetClass.isInstance(result.getBytes())) {
                return (T) result.getBytes();
            }
        }
        if (result.getString() != null && isJsonContent(result)) {
            JavaType javaType = objectMapper.getTypeFactory().constructType(resultType);
            return objectMapper.readValue(result.getString(), javaType);
        }
        if (result.getString() != null) {
            return (T) result.getString();
        }
        if (result.getBytes() != null) {
            return (T) result.getBytes();
        }
        if (result.getByteStream() != null) {
            return (T) result.getByteStream();
        }
        if (result.getCharStream() != null) {
            return (T) result.getCharStream();
        }
        return null;
    }
    /**
     * 判断响应内容是否为JSON格式
     * @param result JResult对象
     * @return true表示是JSON内容，false表示不是
     */
    private boolean isJsonContent(JResult result) {
        String string = result.getString();
        if (string == null || string.trim().isEmpty()) {
            return false;
        }
        String trimmed = string.trim();
        char firstChar = trimmed.charAt(0);
        if (firstChar == '{' || firstChar == '[') {
            try {
                objectMapper.readTree(trimmed);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }
}
