package com.paohaijiao.javelin.test;

import com.paohaijiao.javelin.factory.JLambdaMapperFactory;
import com.paohaijiao.javelin.mapper.JLambdaMapper;
import com.paohaijiao.javelin.session.JSqlSessionFactory;
import com.paohaijiao.javelin.session.impl.DefaultSqlSessionFactory;
import com.paohaijiao.javelin.support.JMappedStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSQLMain {
    public static void main(String[] args) {
        Map<String, JMappedStatement> map=new HashMap<>();
        JSqlSessionFactory sqlSessionFactory =new DefaultSqlSessionFactory(null,map);
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        List<JUser> users = userMapper.query()
                .eq(JUser::getName, "张三")
//                .gt(User::getAge, 18)
//                .orderByDesc(User::getAge)
                .list();

        int affected = userMapper.update()
                .set(JUser::getAge, 25)
                .eq(JUser::getId, 1L)
                .execute();
        JUser newUser = new JUser();
//    newUser.setName("李四");
//    newUser.setAge(30);
//    userMapper.insert(newUser);
    }
}
