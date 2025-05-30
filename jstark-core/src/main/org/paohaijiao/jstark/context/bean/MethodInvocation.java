package org.paohaijiao.jstark.context.bean;

import java.lang.reflect.Method;

public interface MethodInvocation {
    Object proceed() throws Throwable;
    Method getMethod();
    Object[] getArguments();
}
