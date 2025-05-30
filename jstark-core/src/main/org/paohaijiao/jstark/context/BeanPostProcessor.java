package org.paohaijiao.jstark.context;

public interface BeanPostProcessor {
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
