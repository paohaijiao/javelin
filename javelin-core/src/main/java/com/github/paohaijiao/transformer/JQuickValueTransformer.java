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
package com.github.paohaijiao.transformer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.paohaijiao.transformer.type.JQuickJavaTypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.result.factory
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/4/26
 */
public class JQuickValueTransformer {

    private static boolean isMapTypeReference(TypeReference<?> typeReference) {
        Type type = typeReference.getType();
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.constructType(typeReference.getType());
        if (javaType != null && javaType.isMapLikeType()) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type rawType = pType.getRawType();
            if (rawType instanceof Class<?>) {
                return Map.class.isAssignableFrom((Class<?>) rawType);
            }
        } else if (type instanceof Class<?>) {
            return Map.class.isAssignableFrom((Class<?>) type);
        }
        return false;
    }

    public Object transform(String data, JQuickJavaTypeReference<?> typeReference) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (data == null || typeReference == null) {
                return null;
            }
            Type type = typeReference.getRawType();
            JQuickJavaTypeReference stringJTypeReference = JQuickJavaTypeReference.of(String.class);
            JQuickJavaTypeReference charSequenceJTypeReference = JQuickJavaTypeReference.of(CharSequence.class);
            boolean isString = type.equals(stringJTypeReference.getRawType());
            boolean isCharSequence = type.equals(charSequenceJTypeReference.getRawType());
            if (data instanceof String && isString) {
                return data;
            }
            if (data instanceof String && isCharSequence) {
                return data;
            }
            if (data instanceof String) {//data is tring but result not string
                if (isMapTypeReference(typeReference)) {
                    return convertWithGson(data, typeReference);
                }
                return objectMapper.readValue(data, typeReference);
            } else {//data is not tring
                return objectMapper.convertValue(data, typeReference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T convertWithGson(String json, TypeReference<T> jacksonTypeRef) {
        Gson gson = new Gson();
        Type gsonType = convertJacksonTypeToGsonType(jacksonTypeRef);
        return gson.fromJson(json, gsonType);
    }

    private Type convertJacksonTypeToGsonType(TypeReference<?> jacksonTypeRef) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.constructType(jacksonTypeRef.getType());
        if (javaType.isMapLikeType()) {
            JavaType keyType = javaType.getKeyType();
            JavaType valueType = javaType.getContentType();
            return TypeToken.getParameterized(Map.class, convertJacksonTypeToGsonType(keyType), convertJacksonTypeToGsonType(valueType)).getType();
        }

        // 其他类型处理
        return javaType.getRawClass();
    }

    private Type convertJacksonTypeToGsonType(JavaType javaType) {
        if (javaType.isMapLikeType()) {
            return TypeToken.getParameterized(Map.class, convertJacksonTypeToGsonType(javaType.getKeyType()), convertJacksonTypeToGsonType(javaType.getContentType())).getType();
        }

        if (javaType.isCollectionLikeType()) {
            return TypeToken.getParameterized(Collection.class, convertJacksonTypeToGsonType(javaType.getContentType())).getType();
        }

        if (javaType.isArrayType()) {
            return TypeToken.getArray(convertJacksonTypeToGsonType(javaType.getContentType())).getType();
        }

        return javaType.getRawClass();
    }
}

