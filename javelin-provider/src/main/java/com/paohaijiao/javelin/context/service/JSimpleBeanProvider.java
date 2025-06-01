package com.paohaijiao.javelin.context.service;
import com.paohaijiao.javelin.anno.JAutowired;
import com.paohaijiao.javelin.bean.JBeanDefinition;
import com.paohaijiao.javelin.context.JBeanProvider;
import com.paohaijiao.javelin.context.JBeanPostProcessor;
import com.paohaijiao.javelin.context.JMethodInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class JSimpleBeanProvider extends JBaseBeanProvider implements JBeanProvider {







    protected void populateBean(String beanName, JBeanDefinition bd, Object bean) {
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

    protected void initializeBean(String beanName, JBeanDefinition bd, Object bean) {
        if (bd.getInitMethodName() != null) {
            try {
                Method initMethod = bd.getBeanClass().getMethod(bd.getInitMethodName());
                initMethod.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException("invoke the method '" + bd.getInitMethodName() + "' fail", e);
            }
        }
    }

    protected Object resolveBeforeInstantiation(String beanName, JBeanDefinition bd) {
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
