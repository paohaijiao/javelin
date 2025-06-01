package org.paohaijiao.jstark.context;


import org.paohaijiao.jstark.bean.JBeanDefinition;

public interface JBeanProvider {
    /**
     *  register bean
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, JBeanDefinition beanDefinition);

    /**
     * get Bean instance
     */
    <T> T getBean(String beanName, Class<T> requiredType);

    /**
     * Add Bean PostProcessor
     */
    void addBeanPostProcessor(JBeanPostProcessor processor);

    /**
     * registerInterceptor
     */
    void registerInterceptor(String beanName, JMethodInterceptor interceptor);

}
