package com.paohaijiao.javelin.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JTable {
    String value() default "";
}
