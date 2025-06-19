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
package com.paohaijiao.javelin.context;


import com.paohaijiao.javelin.model.JBeanDefinitionModel;

public interface JBeanProvider {
    /**
     *  register bean
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, JBeanDefinitionModel beanDefinition);

    /**
     * get Bean instance
     */
    <T> T getBean(String beanName, Class<T> requiredType);

    /**
     *
     * @param requiredType
     * @return
     * @param <T>
     */
    <T> T getBean( Class<T> requiredType);

    /**
     * Add Bean PostProcessor
     */
    void addBeanPostProcessor(JBeanPostProcessor processor);

    /**
     * registerInterceptor
     */
    void registerInterceptor(String beanName, JMethodInterceptor interceptor);

}
