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
package com.paohaijiao.javelin.test;

import com.paohaijiao.javelin.model.JBeanDefinitionModel;
import com.paohaijiao.javelin.context.JBeanProvider;
import com.paohaijiao.javelin.context.service.JProxyEnhancedBeanProvider;
import com.paohaijiao.javelin.factory.JBeanProviderFactory;

import java.util.Properties;

public class BeanMain {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.setProperty("bean.container.mode", "simple"); // 或 "simple"
        JBeanProvider container = JBeanProviderFactory.createProvider(config);
        JBeanDefinitionModel serviceDef = new JBeanDefinitionModel(ProviderUserServiceImpl.class);
        container.registerBeanDefinition("myService", serviceDef);
        if (container instanceof JProxyEnhancedBeanProvider) {
            container.registerInterceptor("myService", invocation -> {
                System.out.println("拦截方法: " + invocation.getMethod().getName());
                long start = System.currentTimeMillis();
                try {
                    return invocation.proceed();
                } finally {
                    System.out.println("方法执行耗时: " + (System.currentTimeMillis() - start) + "ms");
                }
            });
        }
        ProviderUserService service = container.getBean("myService", ProviderUserService.class);
        ProviderUserService service1 = container.getBean(ProviderUserService.class);
        service.sayHello("haha");
        service1.sayHello("haha1");
    }
}
