package org.paohaijiao.jstark.core;

import org.paohaijiao.jstark.anno.JColumn;
import org.paohaijiao.jstark.anno.JTable;
import org.paohaijiao.jstark.bean.JCondition;
import org.paohaijiao.jstark.bean.JOrder;
import org.paohaijiao.jstark.session.JSqlSession;
import org.paohaijiao.jstark.util.JStringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class JLambdaBaseImpl<T>{
    protected  Class<T> entityClass;
    protected JSqlSession sqlSession;
    protected final List<JCondition> conditions = new ArrayList<>();
    protected final List<JOrder> orders = new ArrayList<>();
    protected String buildSelectSQL() {
        String tableName = getTableName();
        String selectFields = buildSelectFields();
        String whereClause = buildWhereClause();
        String orderByClause = buildOrderByClause();
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(selectFields).append(" FROM ").append(tableName);
        if (JStringUtils.isNotBlank(whereClause)) {
            sql.append(" WHERE ").append(whereClause);
        }
        if (JStringUtils.isNotBlank(orderByClause)) {
            sql.append(" ORDER BY ").append(orderByClause);
        }
        return sql.toString();
    }
    private String getTableName() {
        JTable tableAnnotation = entityClass.getAnnotation(JTable.class);
        if (tableAnnotation != null && !tableAnnotation.value().isEmpty()) {
            return tableAnnotation.value();
        }
        return JStringUtils.camelToUnderline(entityClass.getSimpleName());
    }
    private String buildSelectFields() {
        Field[] fields = entityClass.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    JColumn columnAnnotation = field.getAnnotation(JColumn.class);
                    String columnName = columnAnnotation != null && !columnAnnotation.value().isEmpty()
                            ? columnAnnotation.value()
                            : JStringUtils.camelToUnderline(field.getName());
                    return columnName + " AS " + field.getName();
                })
                .collect(Collectors.joining(", "));
    }
    private String buildWhereClause() {
        if (conditions.isEmpty()) {
            return "";
        }
        return conditions.stream()
                .map(condition -> {
                    String column = condition.getColumn();
                    String operator = condition.getOperator();
                    String paramName = "param_" + column.replace('.', '_');
                    // 处理IN操作符特殊情况
                    if ("IN".equalsIgnoreCase(operator)) {
                        Collection<?> values = (Collection<?>) condition.getValue();
                        String placeholders = values.stream()
                                .map(v -> "#{" + paramName + "}")
                                .collect(Collectors.joining(", "));
                        return column + " IN (" + placeholders + ")";
                    }

                    // 处理LIKE操作符特殊情况
                    if ("LIKE".equalsIgnoreCase(operator)) {
                        return column + " LIKE CONCAT('%', #{" + paramName + "}, '%')";
                    }
                    return column + " " + operator + " #{" + paramName + "}";
                })
                .collect(Collectors.joining(" AND "));
    }
    private String buildOrderByClause() {
        if (orders.isEmpty()) {
            return "";
        }
        return orders.stream()
                .map(order -> {
                    String column = order.getColumn();
                    String direction = order.isAsc() ? "ASC" : "DESC";
                    return column + " " + direction;
                })
                .collect(Collectors.joining(", "));
    }
    protected Map<String, Object> buildParameterMap() {
        Map<String, Object> paramMap = new HashMap<>();
        for (JCondition condition : conditions) {
            String column = condition.getColumn();
            String paramName = "param_" + column.replace('.', '_');
            if ("IN".equalsIgnoreCase(condition.getOperator())) {
                Collection<?> values = (Collection<?>) condition.getValue();
                int index = 0;
                for (Object value : values) {
                    paramMap.put(paramName + "_" + index++, value);
                }
            } else {
                paramMap.put(paramName, condition.getValue());
            }
        }

        return paramMap;
    }

}
