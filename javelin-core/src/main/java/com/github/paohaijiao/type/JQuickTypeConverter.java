package com.github.paohaijiao.type;

/**
 * JQuickRow → Bean 转换的扩展类型转换器（SPI）。
 * <p>
 * 当框架内置转换规则无法处理目标类型时，会按 {@link com.github.paohaijiao.spi.anno.Priority}
 * 优先级依次匹配实现。注册方式：在 classpath 下放置
 * {@code META-INF/services/com.github.paohaijiao.type.JQuickTypeConverter}。
 */
public interface JQuickTypeConverter {

    /** 是否支持转换到该目标类型（targetType 可能为包装类型）。 */
    boolean supports(Class<?> targetType);

    /** 执行转换，value 不会为 null。 */
    Object convert(Object value, Class<?> targetType);
}
