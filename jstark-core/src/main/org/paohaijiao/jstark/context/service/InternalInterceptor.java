package org.paohaijiao.jstark.context.service;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.paohaijiao.jstark.context.CglibMethodInterceptor;

import java.lang.reflect.Method;

public class InternalInterceptor implements MethodInterceptor {
    private final Object targetBean;
    private final CglibMethodInterceptor delegate;

    public InternalInterceptor(Object targetBean, CglibMethodInterceptor delegate) {
        this.targetBean = targetBean;
        this.delegate = delegate;
    }

    @Override
    public Object intercept(Object proxyObj, Method method, Object[] args,
                            MethodProxy methodProxy) throws Throwable {
        return delegate.intercept(proxyObj, method, args, methodProxy, targetBean);
    }
}
