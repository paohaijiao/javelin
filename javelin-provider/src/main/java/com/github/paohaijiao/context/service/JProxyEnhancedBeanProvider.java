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
package com.github.paohaijiao.context.service;

import com.github.paohaijiao.model.JBeanDefinitionModel;
import com.github.paohaijiao.model.JMethodInvocationModel;
import com.github.paohaijiao.context.JMethodInterceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JProxyEnhancedBeanProvider extends JSimpleBeanProvider {

    private final Map<String, JMethodInterceptor> interceptors = new ConcurrentHashMap<>();

    @Override
    protected Object createBean(String beanName, JBeanDefinitionModel bd) {
        Object bean = super.createBean(beanName, bd);
        JMethodInterceptor interceptor = interceptors.get(beanName);
        if (interceptor != null && bd.getBeanClass().getInterfaces().length > 0) {
            return createProxy(bean, interceptor);
        }
        return bean;
    }

    protected Object createProxy(Object target, JMethodInterceptor interceptor) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> interceptor.intercept(new JMethodInvocationModel() {
                    @Override
                    public Object proceed() throws Throwable {
                        return method.invoke(target, args);
                    }

                    @Override
                    public Method getMethod() {
                        return method;
                    }

                    @Override
                    public Object[] getArguments() {
                        return args;
                    }
                }));
    }

    @Override
    public void registerInterceptor(String beanName, JMethodInterceptor interceptor) {
        interceptors.put(beanName, interceptor);
    }
}
