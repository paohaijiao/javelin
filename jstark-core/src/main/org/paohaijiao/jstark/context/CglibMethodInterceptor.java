package org.paohaijiao.jstark.context;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@FunctionalInterface
public interface CglibMethodInterceptor {
    Object intercept(Object proxyObj, Method method, Object[] args,
                     MethodProxy methodProxy, Object targetBean) throws Throwable;
}
