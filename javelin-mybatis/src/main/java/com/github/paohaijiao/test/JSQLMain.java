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
package com.github.paohaijiao.test;

import com.github.paohaijiao.factory.JLambdaMapperFactory;
import com.github.paohaijiao.mapper.JLambdaMapper;
import com.github.paohaijiao.session.JSqlSessionFactory;
import com.github.paohaijiao.session.impl.DefaultSqlSessionFactory;
import com.github.paohaijiao.support.JMappedStatement;

import java.util.HashMap;
import java.util.Map;

public class JSQLMain {
    public static void main(String[] args) {
        Map<String, JMappedStatement> map=new HashMap<>();
        JSqlSessionFactory sqlSessionFactory =new DefaultSqlSessionFactory(null,map);
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
//        List<JUser> users = userMapper.query()
//                .eq(JUser::getName, "张三")
////                .gt(User::getAge, 18)
////                .orderByDesc(User::getAge)
//                .list();

//        int affected = userMapper.update()
//                .set(JUser::getAge, 25)
//                .set(JUser::getId, 25)
//                .set(JUser::getName, 25)
//                .eq(JUser::getId, 1L)
//                .execute();
        JUser newUser = new JUser();
        newUser.setName("李四");
        newUser.setAge(30);
        newUser.setId(1L);
        userMapper.selectById(1);
    }
}
