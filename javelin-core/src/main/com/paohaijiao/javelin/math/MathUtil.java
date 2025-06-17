package com.paohaijiao.javelin.math;

import com.paohaijiao.javelin.exception.Assert;

import java.math.BigDecimal;

public class MathUtil {

    public static BigDecimal op(Object obj1, Object obj2,String op) {
        Assert.notNull(obj1,"obj1 is null");
        Assert.notNull(obj2,"obj2 is null");
        BigDecimal b1 = new BigDecimal(obj1.toString());
        BigDecimal b2 = new BigDecimal(obj2.toString());
        if(op.equals("+")) {
            return b1.add(b2);
        } else if (op.equals("-")) {
            return b1.subtract(b2);
        } else if(op.equals("*")) {
            return b1.multiply(b2);
        }else if(op.equals("/")) {
            return b1.divide(b2);
        }
        Assert.throwNewException("invalid operator");
        return null;
    }
}
