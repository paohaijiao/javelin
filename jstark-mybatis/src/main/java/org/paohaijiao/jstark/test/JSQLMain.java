package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.factory.JLambdaMapperFactory;
import org.paohaijiao.jstark.mapper.JLambdaMapper;
import org.paohaijiao.jstark.session.JSqlSessionFactory;
import org.paohaijiao.jstark.session.impl.DefaultSqlSessionFactory;
import org.paohaijiao.jstark.support.JMappedStatement;

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
