package org.paohaijiao.jstark.context;
import org.paohaijiao.jstark.anno.Autowired;
import org.paohaijiao.jstark.context.bean.BeanDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class BeanContainer {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private final ThreadLocal<Map<String, Boolean>> beansInCreation =
            ThreadLocal.withInitial(HashMap::new);

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalStateException("Bean name '" + beanName + "' 已存在");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    public Object getBean(String beanName) {
        return doGetBean(beanName, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> requiredType) {
        return (T) doGetBean(beanName, requiredType);
    }

    protected Object doGetBean(String beanName, Class<?> requiredType) {
        Object sharedInstance = singletonObjects.get(beanName);
        if (sharedInstance != null) {
            return sharedInstance;
        }

        BeanDefinition bd = beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new IllegalArgumentException("can not find the name '" + beanName + "' 的Bean定义");
        }
        if (requiredType != null && !requiredType.isAssignableFrom(bd.getBeanClass())) {
            throw new IllegalArgumentException("Bean '" + beanName + "' 不是 " + requiredType.getName() + " 类型");
        }

        markBeanAsInCreation(beanName);

        try {
            Object bean = createBean(beanName, bd);
            if (bd.isSingleton()) {
                addSingleton(beanName, bean);
            }
            return bean;
        } finally {
            beansInCreation.get().remove(beanName);
        }
    }

    protected Object createBean(String beanName, BeanDefinition bd) {
        try {
            Object bean = bd.getBeanClass().getDeclaredConstructor().newInstance();
            populateBean(beanName, bd, bean);
            initializeBean(beanName, bd, bean);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(" create Bean '" + beanName + "' fail", e);
        }
    }

    protected void populateBean(String beanName, BeanDefinition bd, Object bean) {
        for (Field field : bd.getBeanClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                try {
                    field.setAccessible(true);
                    Object dependency = getBean(field.getName());
                    field.set(bean, dependency);
                } catch (Exception e) {
                    throw new RuntimeException("inject dependency '" + field.getName() + "' into Bean '" + beanName + "' fail", e);
                }
            }
        }
    }

    /**
     * 初始化 Bean
     */
    protected void initializeBean(String beanName, BeanDefinition bd, Object bean) {
        if (bd.getInitMethodName() != null) {
            try {
                Method initMethod = bd.getBeanClass().getMethod(bd.getInitMethodName());
                initMethod.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException("invoke the initial '" + bd.getInitMethodName() + "' fail", e);
            }
        }
    }

    /**
     * 添加单例 Bean 到缓存
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    /**
     * 标记 Bean 正在创建
     */
    protected void markBeanAsInCreation(String beanName) {
        Map<String, Boolean> inCreation = beansInCreation.get();
        if (inCreation.put(beanName, true) != null) {
            throw new IllegalStateException("检测到循环依赖: " + beanName);
        }
    }
}
