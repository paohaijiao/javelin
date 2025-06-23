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

import com.github.paohaijiao.mapper.JLambdaMapper;
import com.github.paohaijiao.mapper.JLambdaQuery;
import com.github.paohaijiao.mapper.JLambdaUpdate;
import com.github.paohaijiao.session.JSqlSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JLambdaMapperImpl<T> implements JLambdaMapper<T> {
    private final Class<T> entityClass;
    private final JSqlSession sqlSession;

    public JLambdaMapperImpl(Class<T> entityClass, JSqlSession sqlSession) {
        this.entityClass = entityClass;
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(T entity) {
        String sql = new LambdaInsertImpl<>(entityClass, sqlSession).buildInsertSQL();
        System.out.println("insert sql:" + sql);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("entity", entity);
        return sqlSession.insert(null, paramMap);
    }

    @Override
    public int updateById(T entity) {
        return new LambdaUpdateImpl<>(entityClass, sqlSession).updateById();
    }

    @Override
    public int deleteById(Serializable id) {
        return new LambdaDeleteImpl<>(entityClass, sqlSession).deleteById(id);
    }

    @Override
    public T selectById(Serializable id) {
        return new JLambdaQueryImpl<>(entityClass, sqlSession).selectById(id);

    }

    @Override
    public JLambdaQuery<T> query() {
        return new JLambdaQueryImpl<>(entityClass, sqlSession);
    }

    @Override
    public JLambdaUpdate<T> update() {
        return new LambdaUpdateImpl<>(entityClass, sqlSession);
    }

}
