package org.paohaijiao.jstark.context.service;

import org.paohaijiao.jstark.context.MethodInterceptor;
import org.paohaijiao.jstark.context.bean.BeanDefinition;
import org.paohaijiao.jstark.context.bean.MethodInvocation;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyEnhancedBeanContainer extends SimpleBeanContainer{

    private final Map<String, MethodInterceptor> interceptors = new ConcurrentHashMap<>();

    @Override
    protected Object createBean(String beanName, BeanDefinition bd) {
        Object bean = super.createBean(beanName, bd);
        MethodInterceptor interceptor = interceptors.get(beanName);
        if (interceptor != null && bd.getBeanClass().getInterfaces().length > 0) {
            return createProxy(bean, interceptor);
        }
        return bean;
    }

    protected Object createProxy(Object target, MethodInterceptor interceptor) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> interceptor.intercept(new MethodInvocation() {
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
    public void registerInterceptor(String beanName, MethodInterceptor interceptor) {
        interceptors.put(beanName, interceptor);
    }
}
