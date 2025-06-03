package com.paohaijiao.javelin.util;

public class StringUtils {

    public static String trim(String str) {
        if(null==str || "".equals(str)) {
            return str;
        }
        String newStr = str.replaceAll("^['\"]|['\"]$", "");
        return newStr;
    }
}
