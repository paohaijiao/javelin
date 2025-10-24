package com.github.paohaijiao.spi.anno;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Priority {
    /**
     * 优先级值
     * 推荐使用范围：1-10 (高优先级), 11-50 (中优先级), 51-100 (低优先级)
     * @return 优先级数值
     */
    int value();

    /**
     * 优先级名称（可选）
     * @return 优先级名称
     */
    String name() default "";
}
