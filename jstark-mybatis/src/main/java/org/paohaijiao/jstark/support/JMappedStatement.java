package org.paohaijiao.jstark.support;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.List;

@Data
public class JMappedStatement {
    private String id;
    private String sql;
    private Class<?> resultType;
    private Type resultGenericType;
    private List<JParameterMapping> parameterMappings;

    public Type getResultGenericType() {
        return resultGenericType != null ? resultGenericType : resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
        this.resultGenericType = resultType;
    }

    public void setResultGenericType(Type resultGenericType) {
        this.resultGenericType = resultGenericType;
    }
}
