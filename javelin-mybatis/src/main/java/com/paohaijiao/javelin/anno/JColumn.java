package com.paohaijiao.javelin.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JColumn {
    String value() default "";
    boolean id() default false;
}
