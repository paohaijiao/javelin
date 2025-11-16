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

import com.github.paohaijiao.context.service.JCglibBeanProvider;
import com.github.paohaijiao.model.JBeanDefinitionModel;

public class CglibContainerExample {
    public static void main(String[] args) {
        JCglibBeanProvider container = new JCglibBeanProvider();
        JBeanDefinitionModel serviceDef = new JBeanDefinitionModel(ProviderUserServiceImpl.class);
        container.registerBeanDefinition("myService", serviceDef);
        container.registerInterceptor("myService",
                (proxyObj, method, args1, methodProxy, targetBean) -> {
                    System.out.println("[CGLIB] Before: " + method.getName());
                    long start = System.currentTimeMillis();
                    try {
                        Object result = methodProxy.invoke(targetBean, args1);
                        System.out.printf("[CGLIB] After: %s (cost: %dms)\n",
                                method.getName(),
                                System.currentTimeMillis() - start);
                        return result;
                    } catch (Exception e) {
                        System.err.println("[CGLIB] Error: " + e.getMessage());
                        throw e;
                    }
                });
        ProviderUserServiceImpl service = container.getBean("myService", ProviderUserServiceImpl.class);
        service.sayHello("helloqsw");
    }
}
