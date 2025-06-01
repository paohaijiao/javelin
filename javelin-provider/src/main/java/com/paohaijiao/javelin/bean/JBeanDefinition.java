package com.paohaijiao.javelin.bean;

public class JBeanDefinition {
    private Class<?> beanClass;
    private boolean singleton = true;
    private String initMethodName;

    public JBeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() { return beanClass; }
    public boolean isSingleton() { return singleton; }
    public void setSingleton(boolean singleton) { this.singleton = singleton; }
    public String getInitMethodName() { return initMethodName; }
    public void setInitMethodName(String initMethodName) { this.initMethodName = initMethodName; }
}
