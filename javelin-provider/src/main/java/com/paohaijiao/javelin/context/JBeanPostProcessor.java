package com.paohaijiao.javelin.context;

public interface JBeanPostProcessor {
    /**
     * BeforeInstantiation
     * @param beanClass
     * @param beanName
     * @return
     */
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) { return null; }

    /**
     * BeforeInitialization
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) { return bean; }

    /**
     * AfterInitialization
     * @param bean
     * @param beanName
     * @return
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) { return bean; }
}
