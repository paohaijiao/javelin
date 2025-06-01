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
public class JSimpleBeanContainer implements JBeanProvider {

    private final Map<String, JBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final List<JBeanPostProcessor> beanPostProcessors = new ArrayList<>();

    @Override
    public void registerBeanDefinition(String beanName, JBeanDefinition beanDefinition) {
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

    protected Object doGetBean(String beanName) {
        Object sharedInstance = singletonObjects.get(beanName);
        if (sharedInstance != null) {
            return sharedInstance;
        }
        JBeanDefinition bd = beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new IllegalArgumentException("can not find the  '" + beanName + "' Bean define");
        }
        Object bean = createBean(beanName, bd);
        if (bd.isSingleton()) {
            addSingleton(beanName, bean);
        }
        return bean;
    }

    protected Object createBean(String beanName, JBeanDefinition bd) {
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

    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    @Override
    public void addBeanPostProcessor(JBeanPostProcessor processor) {
        this.beanPostProcessors.add(processor);
    }

    @Override
    public void registerInterceptor(String beanName, JMethodInterceptor interceptor) {
        throw new UnsupportedOperationException("simple container not support registerInterceptor");
    }
}
