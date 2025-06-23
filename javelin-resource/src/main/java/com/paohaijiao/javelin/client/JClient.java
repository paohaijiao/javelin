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
package com.paohaijiao.javelin.client;


import com.paohaijiao.javelin.adaptor.JAdaptor;
import com.paohaijiao.javelin.resouce.JReader;
import com.paohaijiao.javelin.resouce.impl.JDataBaseReader;
import com.paohaijiao.javelin.resouce.impl.JFileReader;

public class JClient {
    public static void main(String[] args) {
        JReader fileReader = new JFileReader("data/rule.txt");
        JAdaptor context = new JAdaptor(fileReader);
        System.out.println(context.getRuleContent());
        JReader dbReader = new JDataBaseReader(
                "jdbc:mysql://localhost:3306/mydb",
                "com.mysql.jdbc.Driver",
                "xxx",
                "xxx",
                "SELECT * FROM xxx"
        );
        context.setDataReader(dbReader);
        System.out.println(context.getRuleContent());
    }
}
