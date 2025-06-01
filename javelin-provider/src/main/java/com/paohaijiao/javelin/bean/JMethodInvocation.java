package com.paohaijiao.javelin.bean;

import java.lang.reflect.Method;

public interface JMethodInvocation {
    Object proceed() throws Throwable;
    Method getMethod();
    Object[] getArguments();
}
