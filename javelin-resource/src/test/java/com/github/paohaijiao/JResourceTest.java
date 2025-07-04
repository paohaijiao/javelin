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

import com.github.paohaijiao.config.JEnvironmentAware;
import org.junit.Test;

import java.io.IOException;

/**
 * packageName com.github.paohaijiao
 *
 * @author Martin
 * @version 1.0.0
 * @className JResourceTest
 * @date 2025/6/24
 * @description
 */
public class JResourceTest {
    @Test
    public void test() throws IOException {
        JEnvironmentAware configLoader = new JEnvironmentAware();
        System.out.println("Dev Environment:");
        printConfigs(configLoader);
        configLoader.setActiveProfile("prod");
        System.out.println("\nProd Environment:");
        printConfigs(configLoader);
    }
    private static void printConfigs(JEnvironmentAware configLoader) {
        System.out.println("App Name: " + configLoader.getProperty("app.name"));
        System.out.println("DB URL: " + configLoader.getProperty("database.url"));
        System.out.println("DB Username: " + configLoader.getProperty("database.username"));
        System.out.println("DB Pool Size: " + configLoader.getProperty("database.pool-size"));
    }
}
