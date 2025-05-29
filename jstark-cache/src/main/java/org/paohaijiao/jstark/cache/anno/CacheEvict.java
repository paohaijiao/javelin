package org.paohaijiao.jstark.cache.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {
    String[] value();
    String key() default "";
    String condition() default "";
    boolean allEntries() default false;
    boolean beforeInvocation() default false;
}
