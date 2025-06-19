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
package com.paohaijiao.javelin.session.impl;

import com.paohaijiao.javelin.session.JSqlSession;
import com.paohaijiao.javelin.session.JSqlSessionFactory;
import com.paohaijiao.javelin.support.JMappedStatement;

import javax.security.auth.login.Configuration;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DefaultSqlSessionFactory implements JSqlSessionFactory {
    private final DataSource dataSource;
    private final Map<String, JMappedStatement> mappedStatements;

    public DefaultSqlSessionFactory(DataSource dataSource,
                                    Map<String, JMappedStatement> mappedStatements) {
        this.dataSource = dataSource;
        this.mappedStatements = mappedStatements;
    }

    @Override
    public JSqlSession openSession() {
        try {
            //dataSource.getConnection()
            return new DefaultSqlSession(dataSource.getConnection(), mappedStatements, false);
        } catch (Exception e) {
            throw new RuntimeException("Cannot open connection", e);
        }
    }

    @Override
    public JSqlSession openSession(boolean autoCommit) {
        try {
            return new DefaultSqlSession(dataSource.getConnection(), mappedStatements, autoCommit);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot open connection", e);
        }
    }

    @Override
    public JSqlSession openSession(Connection connection) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }
}
