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
import com.github.paohaijiao.format.JSqlFormatter;
import com.github.paohaijiao.model.JCondition;
import com.github.paohaijiao.function.JSFunction;
import com.github.paohaijiao.mapper.JLambdaQuery;
import com.github.paohaijiao.connection.JSqlConnection;
import com.github.paohaijiao.model.JKeyValue;
import com.github.paohaijiao.statement.JNamedParameterPreparedStatement;
import com.github.paohaijiao.util.JStringUtils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JLambdaQueryImpl<T> extends JLambdaBaseImpl<T> implements JLambdaQuery<T> {

    public JLambdaQueryImpl(Class<T> entityClass, JSqlConnection sqlConnection) {
        this.entityClass = entityClass;
        this.sqlConnection = sqlConnection;
    }
    public T selectById(Serializable id){
        String selectSql = "select * from  %s  where %s = %s";
        String tableName = getTableName();
        String idClause=this.getIdFieldName();
        String value="#{"+idClause+"}";
        String sql=String.format(selectSql, tableName, idClause,value);
        try {
            JNamedParameterPreparedStatement namedParameterPreparedStatement = new JNamedParameterPreparedStatement(sqlConnection.getConnection(), sql);
            JKeyValue model=new JKeyValue();
            model.setNum(1);
            model.setKey(idClause);
            model.setValue(id);
            namedParameterPreparedStatement.setParameter(model);
            ResultSet resultSet=namedParameterPreparedStatement.executeQuery();
            resultSet.next();
            T t= resultSetToObject(resultSet,entityClass);
           return t;
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }
    @Override
    public JLambdaQuery<T> eq(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "=", value));
        return this;
    }

    @Override
    public JLambdaQuery<T> ne(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "!=", value));
        return this;
    }

    @Override
    public JLambdaQuery<T> gt(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), ">", value));
        return this;
    }

    @Override
    public JLambdaQuery<T> ge(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), ">=", value));
        return this;
    }

    @Override
    public JLambdaQuery<T> lt(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "<", value));
        return this;
    }

    @Override
    public JLambdaQuery<T> le(JSFunction<T, ?> column, Object value) {
        conditions.add(new JCondition(getColumnName(column), "<=", value));
        return this;
    }

    @Override
    public JLambdaQuery<T> like(JSFunction<T, ?> column, String value) {
        conditions.add(new JCondition(getColumnName(column), "like", value));
        return this;
    }

    @Override
    public JLambdaQuery<T> in(JSFunction<T, ?> column, Collection<?> values) {
        conditions.add(new JCondition(getColumnName(column), "in", values));
        return this;
    }

    @Override
    public JLambdaQuery<T> orderByAsc(JSFunction<T, ?> column) {
        return null;
    }

    @Override
    public JLambdaQuery<T> orderByDesc(JSFunction<T, ?> column) {
        return null;
    }

    @Override
    public List<T> list() {
        List<T> list=new ArrayList<>();
        String sql = buildSelectSQL();
        List<JCondition> condition=this.conditions;
        try {
            JNamedParameterPreparedStatement namedParameterPreparedStatement = new JNamedParameterPreparedStatement(sqlConnection.getConnection(), sql);
            for (int i=0;i<condition.size();i++){
                JKeyValue model=new JKeyValue();
                model.setNum(i+1);
                model.setKey(condition.get(i).getColumn());
                model.setValue(condition.get(i).getValue());
                namedParameterPreparedStatement.setParameter(model);
            }
            ResultSet resultSet=namedParameterPreparedStatement.executeQuery();

            while(resultSet.next()){
                T t= resultSetToObject(resultSet,entityClass);
                list.add(t);
            }
            return list;

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return list;
    }

    @Override
    public T one() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    protected String getColumnName(JSFunction<T, ?> column) {
        try {
            Method writeReplace = column.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(column);
            String methodName = lambda.getImplMethodName();
            if (methodName.startsWith("get")) {
                methodName = methodName.substring(3);
            } else if (methodName.startsWith("is")) {
                methodName = methodName.substring(2);
            }

            String fieldName = JStringUtils.uncapitalize(methodName);
            Field field = entityClass.getDeclaredField(fieldName);
            JColumn columnAnnotation = field.getAnnotation(JColumn.class);
            return columnAnnotation != null && !columnAnnotation.value().isEmpty()
                    ? columnAnnotation.value()
                    : JStringUtils.camelToUnderline(fieldName);
        } catch (Exception e) {
            throw new RuntimeException("解析Lambda表达式失败", e);
        }
    }
}
