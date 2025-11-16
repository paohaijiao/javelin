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

import com.github.paohaijiao.anno.JAutowired;
import com.github.paohaijiao.context.JBeanPostProcessor;
import com.github.paohaijiao.context.JBeanProvider;
import com.github.paohaijiao.model.JBeanDefinitionModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JSimpleBeanProvider extends JBaseBeanProvider implements JBeanProvider {


    protected void populateBean(String beanName, JBeanDefinitionModel bd, Object bean) {
        for (Field field : bd.getBeanClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JAutowired.class)) {
                try {
                    field.setAccessible(true);
                    Object dependency = getBean(field.getName(), field.getType());
                    field.set(bean, dependency);
                } catch (Exception e) {
                    throw new RuntimeException("inject Dependency '" + field.getName() + "' into Bean '" + beanName + "' fail", e);
                }
            }
        }
    }

    protected void initializeBean(String beanName, JBeanDefinitionModel bd, Object bean) {
        if (bd.getInitMethodName() != null) {
            try {
                Method initMethod = bd.getBeanClass().getMethod(bd.getInitMethodName());
                initMethod.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException("invoke the method '" + bd.getInitMethodName() + "' fail", e);
            }
        }
    }

    protected Object resolveBeforeInstantiation(String beanName, JBeanDefinitionModel bd) {
        for (JBeanPostProcessor bp : beanPostProcessors) {
            Object result = bp.postProcessBeforeInstantiation(bd.getBeanClass(), beanName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        Object result = bean;
        for (JBeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    protected Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        Object result = bean;
        for (JBeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }


}
