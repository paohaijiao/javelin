package com.paohaijiao.javelin.context;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@FunctionalInterface
public interface JCglibMethodInterceptor {
    Object intercept(Object proxyObj, Method method, Object[] args,
                     MethodProxy methodProxy, Object targetBean) throws Throwable;
}
