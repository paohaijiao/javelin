package com.paohaijiao.javelin.core;

import com.paohaijiao.javelin.anno.JColumn;
import com.paohaijiao.javelin.bean.JCondition;
import com.paohaijiao.javelin.function.JSFunction;
import com.paohaijiao.javelin.mapper.JLambdaUpdate;
import com.paohaijiao.javelin.session.JSqlSession;
import com.paohaijiao.javelin.util.JStringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
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
