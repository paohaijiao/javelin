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
package com.github.paohaijiao.factory;

import com.github.paohaijiao.context.JBeanProvider;
import com.github.paohaijiao.context.service.JProxyEnhancedBeanProvider;
import com.github.paohaijiao.context.service.JSimpleBeanProvider;

import java.util.Properties;

public class JBeanProviderFactory {
    /**
     * create container instance based on configuration
     */
    public static JBeanProvider createProvider(Properties config) {
        String mode = config.getProperty("bean.container.mode", "simple");
        switch (mode.toLowerCase()) {
            case "proxy":
                return new JProxyEnhancedBeanProvider();
            case "simple":
            default:
                return new JSimpleBeanProvider();
        }
    }

    /**
     * create default container
     */
    public static JBeanProvider createDefaultContainer() {
        return createProvider(new Properties());
    }
}
