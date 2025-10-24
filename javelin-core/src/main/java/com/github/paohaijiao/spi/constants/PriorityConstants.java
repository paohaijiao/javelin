package com.github.paohaijiao.spi.constants;

/**
 * 优先级常量定义
 * 数值越小，优先级越高
 */
public final class PriorityConstants {

    private PriorityConstants() {
        throw new AssertionError("常量类不允许实例化");
    }

    // 系统级别（最高优先级）
    public static final int SYSTEM_HIGHEST = 1;
    public static final int SYSTEM_HIGH = 10;
    public static final int SYSTEM_MEDIUM = 20;
    public static final int SYSTEM_LOW = 30;

    // 应用级别
    public static final int APPLICATION_HIGHEST = 100;
    public static final int APPLICATION_HIGH = 200;
    public static final int APPLICATION_MEDIUM = 300;
    public static final int APPLICATION_LOW = 400;

    // 业务级别
    public static final int BUSINESS_HIGHEST = 1000;
    public static final int BUSINESS_HIGH = 2000;
    public static final int BUSINESS_MEDIUM = 3000;
    public static final int BUSINESS_LOW = 4000;

    // 用户级别
    public static final int USER_HIGHEST = 5000;
    public static final int USER_HIGH = 6000;
    public static final int USER_MEDIUM = 7000;
    public static final int USER_LOW = 8000;

    // 默认优先级
    public static final int DEFAULT = 5000;

    // 最低优先级
    public static final int LOWEST = 10000;

    /**
     * 获取优先级名称
     */
    public static String getPriorityName(int priority) {
        if (priority <= SYSTEM_LOW) return "SYSTEM";
        if (priority <= APPLICATION_LOW) return "APPLICATION";
        if (priority <= BUSINESS_LOW) return "BUSINESS";
        if (priority <= USER_LOW) return "USER";
        return "LOWEST";
    }
}