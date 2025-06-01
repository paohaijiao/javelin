package com.paohaijiao.javelin.context.service;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import com.paohaijiao.javelin.context.JCglibMethodInterceptor;

import java.lang.reflect.Method;

public class JInternalInterceptor implements MethodInterceptor {
    private final Object targetBean;
    private final JCglibMethodInterceptor delegate;

    public JInternalInterceptor(Object targetBean, JCglibMethodInterceptor delegate) {
        this.targetBean = targetBean;
        this.delegate = delegate;
    }

    @Override
    public Object intercept(Object proxyObj, Method method, Object[] args,
                            MethodProxy methodProxy) throws Throwable {
        return delegate.intercept(proxyObj, method, args, methodProxy, targetBean);
    }
}
