package com.paohaijiao.javelin.enums;

import com.paohaijiao.javelin.exception.Assert;

public enum JMethodEnums {
    length("length");
    private String method;

    JMethodEnums(String method) {
        this.method = method;
    }

    /**
     * 根据传入的方法获取枚举类
     * @param method
     * @return
     */
    public static JMethodEnums methodOf(String method) {
        for (JMethodEnums jMethodEnums : JMethodEnums.values()) {
            if (jMethodEnums.method.equals(method)) {
                return jMethodEnums;
            }
        }
        Assert.throwNewException("invalid method enum");
        return null;
    }

}
