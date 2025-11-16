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

import com.github.paohaijiao.context.JBeanPostProcessor;
import com.github.paohaijiao.context.JBeanProvider;
import com.github.paohaijiao.context.JMethodInterceptor;
import com.github.paohaijiao.model.JBeanDefinitionModel;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JBaseBeanProvider implements JBeanProvider {
    protected final Map<String, JBeanDefinitionModel> beanDefinitionMap = new ConcurrentHashMap<>();
    protected final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    protected final List<JBeanPostProcessor> beanPostProcessors = new ArrayList<>();

    @Override
    public void registerBeanDefinition(String beanName, JBeanDefinitionModel beanDefinition) {
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalStateException("Bean name '" + beanName + "' exists");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> requiredType) {
        Object bean = doGetBean(beanName);
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw new IllegalArgumentException("Bean '" + beanName + "' is not   " + requiredType.getName() + " type");
        }
        return (T) bean;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        for (Object obj : singletonObjects.values()) {
            if (requiredType.isInstance(obj)) {
                return requiredType.cast(obj);
            }
        }
        return null;
    }

    protected Object doGetBean(String beanName) {
        Object sharedInstance = singletonObjects.get(beanName);
        if (sharedInstance != null) {
            return sharedInstance;
        }
        JBeanDefinitionModel bd = beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new IllegalArgumentException("can not find the  '" + beanName + "' Bean define");
        }
        Object bean = createBean(beanName, bd);
        if (bd.isSingleton()) {
            addSingleton(beanName, bean);
        }
        return bean;
    }

    protected Object createBean(String beanName, JBeanDefinitionModel bd) {
        try {
            if (bd.getBeanClass().isInterface() || bd.getBeanClass().isPrimitive() || bd.getBeanClass().isArray()) {
                throw new IllegalArgumentException("can not instance due to class is interface or Primitive or array " + bd.getBeanClass().getName());
            }
            //attemp get no argues method parameter
            Constructor<?>[] constructors = bd.getBeanClass().getDeclaredConstructors();
            //no arguement param is best option
            Constructor<?> constructorToUse = null;
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    constructorToUse = constructor;
                    break;
                }
            }
            // if no argue param constructor then choose first constructors
            if (constructorToUse == null && constructors.length > 0) {
                constructorToUse = constructors[0];
                throw new UnsupportedOperationException("not support constructor");
            }
            if (constructorToUse == null) {
                throw new IllegalStateException("no available constructor");
            }
            constructorToUse.setAccessible(true);
            Object bean = constructorToUse.newInstance();
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("create Bean '" + beanName + "' fail", e);
        }
    }

    @Override
    public void addBeanPostProcessor(JBeanPostProcessor processor) {
        this.beanPostProcessors.add(processor);
    }

    @Override
    public void registerInterceptor(String beanName, JMethodInterceptor interceptor) {
        throw new UnsupportedOperationException("simple container not support registerInterceptor");
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }
}
