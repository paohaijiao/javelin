package com.paohaijiao.javelin.context;


import com.paohaijiao.javelin.bean.JMethodInvocation;

public interface JMethodInterceptor {

    Object intercept(JMethodInvocation invocation) throws Throwable;
}
