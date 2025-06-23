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
package com.github.paohaijiao.core;

import com.github.paohaijiao.anno.JColumn;
import com.github.paohaijiao.session.JSqlSession;
import com.github.paohaijiao.util.JStringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LambdaInsertImpl<T> extends JLambdaBaseImpl<T>  {
    private final Map<String, Object> updateValues = new HashMap<>();

    public LambdaInsertImpl(Class<T> entityClass, JSqlSession sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }
    public String buildInsertSQL() {
        String tableName = getTableName();
        String columns = buildInsertColumns();
        String values = buildInsertValues();
        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
    }
    private String buildInsertColumns() {
        Field[] fields = entityClass.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    JColumn columnAnnotation = field.getAnnotation(JColumn.class);
                    return columnAnnotation != null && !columnAnnotation.value().isEmpty()
                            ? columnAnnotation.value()
                            : JStringUtils.camelToUnderline(field.getName());
                })
                .collect(Collectors.joining(", "));
    }

    private String buildInsertValues() {
        Field[] fields = entityClass.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    String fieldName = field.getName();
                    return "#{" + fieldName + "}";
                })
                .collect(Collectors.joining(", "));
    }

    private Map<String, Object> buildInsertParameterMap(T entity) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("entity", entity);
        return paramMap;
    }

}
