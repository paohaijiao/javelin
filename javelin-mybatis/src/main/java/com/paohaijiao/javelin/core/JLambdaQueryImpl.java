package com.paohaijiao.javelin.core;

import com.paohaijiao.javelin.anno.JColumn;
import com.paohaijiao.javelin.bean.JCondition;
import com.paohaijiao.javelin.function.JSFunction;
import com.paohaijiao.javelin.mapper.JLambdaQuery;
import com.paohaijiao.javelin.session.JSqlSession;
import com.paohaijiao.javelin.util.JStringUtils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class JLambdaQueryImpl<T> extends JLambdaBaseImpl<T> implements JLambdaQuery<T> {

    public JLambdaQueryImpl(Class<T> entityClass, JSqlSession sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }
    public T selectById(Serializable id){
        String selectSql = "select * from  %s  where %s = %s";
        String tableName = getTableName();
        String idClause=this.getIdFieldName();
        String sql=String.format(selectSql, tableName, idClause,id);
        System.out.println(sql);
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
        String sql = buildSelectSQL();
        System.out.println("sql:"  +sql);
        //getStatement(sql);
        return sqlSession.selectList(null, buildParameterMap());
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
