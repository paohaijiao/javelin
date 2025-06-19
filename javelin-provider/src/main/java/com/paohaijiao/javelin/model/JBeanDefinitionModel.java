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
package com.paohaijiao.javelin.model;

public class JBeanDefinitionModel {
    private Class<?> beanClass;
    private boolean singleton = true;
    private String initMethodName;

    public JBeanDefinitionModel(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() { return beanClass; }
    public boolean isSingleton() { return singleton; }
    public void setSingleton(boolean singleton) { this.singleton = singleton; }
    public String getInitMethodName() { return initMethodName; }
    public void setInitMethodName(String initMethodName) { this.initMethodName = initMethodName; }
}
