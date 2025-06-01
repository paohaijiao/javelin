package org.paohaijiao.jstark.context;


import org.paohaijiao.jstark.bean.JMethodInvocation;

public interface JMethodInterceptor {

    Object intercept(JMethodInvocation invocation) throws Throwable;
}
