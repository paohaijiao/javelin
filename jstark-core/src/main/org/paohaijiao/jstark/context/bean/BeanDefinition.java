package org.paohaijiao.jstark.context.bean;

public class BeanDefinition {
    private Class<?> beanClass;
    private boolean singleton = true;
    private String initMethodName;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    // getter 和 setter 方法
    public Class<?> getBeanClass() { return beanClass; }
    public boolean isSingleton() { return singleton; }
    public void setSingleton(boolean singleton) { this.singleton = singleton; }
    public String getInitMethodName() { return initMethodName; }
    public void setInitMethodName(String initMethodName) { this.initMethodName = initMethodName; }
}
