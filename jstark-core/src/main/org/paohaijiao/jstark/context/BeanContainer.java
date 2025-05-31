package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.bean.BeanDefinition;

public interface BeanContainer {
    /**
     *  register bean
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * get Bean instance
     */
    <T> T getBean(String beanName, Class<T> requiredType);

    /**
     * Add Bean PostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor processor);

    /**
     * registerInterceptor
     */
    void registerInterceptor(String beanName, MethodInterceptor interceptor);

}
