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
package com.github.paohaijiao;

import com.github.paohaijiao.ds.JDBCBaseConnectionConfig;
import com.github.paohaijiao.ds.impl.JBasicJDBConnectionConfig;
import com.github.paohaijiao.factory.JLambdaMapperFactory;
import com.github.paohaijiao.mapper.JLambdaMapper;
import com.github.paohaijiao.connection.JSqlConnectionFactory;
import com.github.paohaijiao.connection.impl.DefaultSqlConnectionactory;
import com.github.paohaijiao.support.JMappedStatement;
import com.github.paohaijiao.model.JUser;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JSQLMain {

    private DataSource getDBConfig() throws ClassNotFoundException, SQLException {
         String userName="system";
         String password="sqwswqswqswqswq";
         String clazz="com.kingbase8.Driver";
         String url="jdbc:kingbase8://127.0.0.1:54323/?currentSchema=public";
        JDBCBaseConnectionConfig config=new JBasicJDBConnectionConfig(clazz,url,userName,password);
        return config.createDataSource();
    }


    @Test
    public void insert() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo=new JUser();
        userPo.setId(1L);
        userPo.setName("kimoo");
        userPo.setAge(12);
        int i=userMapper.insert(userPo);
        System.out.println(i);
    }
    @Test
    public void updateById() throws IOException, SQLException, ClassNotFoundException {
        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig());
        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
        JUser userPo=new JUser();
        userPo.setId(1L);
        userPo.setName("kimoo1");
        userPo.setAge(12);
        int i=userMapper.updateById(userPo);
        System.out.println(i);
    }

//    @Test
//    public void selectById() throws IOException, SQLException, ClassNotFoundException {
//        Map<String, JMappedStatement> map=new HashMap<>();
//        JSqlConnectionFactory sqlSessionFactory =new DefaultSqlConnectionactory(getDBConfig(),map);
//        JLambdaMapperFactory factory = new JLambdaMapperFactory(sqlSessionFactory);
//        JLambdaMapper<JUser> userMapper = factory.createMapper(JUser.class);
//        JUser userPo=userMapper.selectById(1);
//        System.out.println(userPo);
//    }


}
