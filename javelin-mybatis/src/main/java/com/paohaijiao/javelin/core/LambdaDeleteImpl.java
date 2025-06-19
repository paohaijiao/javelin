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
