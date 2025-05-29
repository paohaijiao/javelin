package org.paohaijiao.jstark.cache.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CachePut {
    String[] value();
    String key() default "";
    String condition() default "";
    String unless() default "";
}
