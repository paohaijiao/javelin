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

import com.github.paohaijiao.context.JCglibMethodInterceptor;
import com.github.paohaijiao.model.JBeanDefinitionModel;
import net.sf.cglib.proxy.Enhancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JCglibBeanProvider extends JSimpleBeanProvider {
    private final Map<String, JCglibMethodInterceptor> interceptors = new ConcurrentHashMap<>();

    /**
     * registration method interceptor
     */
    public void registerInterceptor(String beanName, JCglibMethodInterceptor interceptor) {
        interceptors.put(beanName, interceptor);
    }

    @Override
    protected Object createBean(String beanName, JBeanDefinitionModel bd) {
        Object rawBean = super.createBean(beanName, bd);
        JCglibMethodInterceptor interceptor = interceptors.get(beanName);
        if (interceptor != null) {
            return createCglibProxy(bd.getBeanClass(), rawBean, interceptor);
        }
        return rawBean;
    }

    /**
     * create CGLIB proxy object
     */
    protected Object createCglibProxy(Class<?> targetClass, Object targetBean,
                                      JCglibMethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new JInternalInterceptor(targetBean, interceptor));
        return enhancer.create();
    }
}
