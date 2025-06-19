package com.paohaijiao.javelin.enums;

import com.paohaijiao.javelin.exception.Assert;
import lombok.Getter;

@Getter
public enum JMethodEnums {
    length("length"),
    toInteger("toInteger"),
    toString("toString"),
    toDouble("toDouble"),
    toFloat("toFloat"),
    toLower("toLower"),
    toUpper("toUpper"),
    ceil("ceil"),
    floor("floor"),
    contains("contains"),
    join("join"),
    split("split"),
    trans("trans"),
    dateFormat("dateFormat"),
    parseToDate("parseToDate"),
    startsWith("startsWith"),
    endsWith("endsWith"),
    round("round"),
    sum("sum"),
    max("max"),
    min("min"),
    avg("avg"),
    substring("substring"),
    replace("replace");
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
