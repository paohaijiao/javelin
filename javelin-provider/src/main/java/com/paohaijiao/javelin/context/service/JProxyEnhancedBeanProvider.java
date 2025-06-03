package com.paohaijiao.javelin.context.service;

import com.paohaijiao.javelin.bean.JBeanDefinition;
import com.paohaijiao.javelin.bean.JMethodInvocation;
import com.paohaijiao.javelin.context.JMethodInterceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JProxyEnhancedBeanProvider extends JSimpleBeanProvider {

    private final Map<String, JMethodInterceptor> interceptors = new ConcurrentHashMap<>();

    @Override
    protected Object createBean(String beanName, JBeanDefinition bd) {
        Object bean = super.createBean(beanName, bd);
        JMethodInterceptor interceptor = interceptors.get(beanName);
        if (interceptor != null && bd.getBeanClass().getInterfaces().length > 0) {
            return createProxy(bean, interceptor);
        }
        return bean;
    }

    protected Object createProxy(Object target, JMethodInterceptor interceptor) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> interceptor.intercept(new JMethodInvocation() {
                    @Override
                    public Object proceed() throws Throwable {
                        return method.invoke(target, args);
                    }

                    @Override
                    public Method getMethod() {
                        return method;
                    }

                    @Override
                    public Object[] getArguments() {
                        return args;
                    }
                }));
    }

    @Override
    public void registerInterceptor(String beanName, JMethodInterceptor interceptor) {
        interceptors.put(beanName, interceptor);
    }
}
