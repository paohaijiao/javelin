package org.paohaijiao.jstark.context;

import org.paohaijiao.jstark.context.bean.MethodInvocation;

public interface MethodInterceptor {
    Object intercept(MethodInvocation invocation) throws Throwable;
}
