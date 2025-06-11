package com.paohaijiao.javelin.core;

import com.paohaijiao.javelin.anno.JColumn;
import com.paohaijiao.javelin.session.JSqlSession;
import com.paohaijiao.javelin.util.JStringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LambdaDeleteImpl<T> extends JLambdaBaseImpl<T>  {
    private final Map<String, Object> updateValues = new HashMap<>();

    public LambdaDeleteImpl(Class<T> entityClass, JSqlSession sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }
    public int deleteById(Serializable id){
        String deleteSql = "delete * from  %s  where %s = %s";
        String tableName = getTableName();
        String idClause=this.getIdFieldName();
        String sql=String.format(deleteSql, tableName, idClause,id);
        System.out.println(sql);
        return 1;
    }


}
