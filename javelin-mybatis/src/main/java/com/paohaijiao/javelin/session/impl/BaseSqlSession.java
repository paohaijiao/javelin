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
package com.paohaijiao.javelin.session.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseSqlSession {
    /**
     * convert resultset to Objects
     * @param rs
     * @param resultType
     * @return
     * @param <E>
     * @throws SQLException
     */
    protected  <E> List<E> resultSetToObjects(ResultSet rs,
                                              Class<?> resultType,
                                              Type genericType) throws SQLException {
        List<E> results = new ArrayList<>();
        if (rs == null || resultType == null) {
            return results;
        }
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (rs.next()) {
            try {
                if (isSimpleType(resultType)) {
                    results.add((E) handleSimpleType(rs, 1, resultType));
                    continue;
                }
                E obj = (E) resultType.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    setProperty(obj, columnName, rs.getObject(i));
                }
                results.add(obj);
            } catch (Exception e) {
                throw new RuntimeException("Error mapping result set to object", e);
            }
        }
        return results;
    }
    protected String columnNameToFieldName(String columnName) {
        String[] parts = columnName.split("_");
        StringBuilder sb = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1).toLowerCase());
        }
        return sb.toString();
    }
    protected boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class ||
                type == Date.class;
    }

    protected Object handleSimpleType(ResultSet rs, int index, Class<?> type) throws SQLException {
        if (type == int.class || type == Integer.class) {
            return rs.getInt(index);
        } else if (type == long.class || type == Long.class) {
            return rs.getLong(index);
        }
        return rs.getObject(index);
    }

    protected void setProperty(Object obj, String columnName, Object value) {
        try {
            String fieldName = columnNameToFieldName(columnName);
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            if (value != null) {
                if (field.getType() == Integer.class || field.getType() == int.class) {
                    value = ((Number) value).intValue();
                } else if (field.getType() == Long.class || field.getType() == long.class) {
                    value = ((Number) value).longValue();
                }
            }

            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
