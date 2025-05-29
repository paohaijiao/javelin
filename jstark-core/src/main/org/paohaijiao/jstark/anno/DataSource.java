package org.paohaijiao.jstark.anno;
import org.paohaijiao.jstark.enums.JStarkSqlType;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    String name() default "default";


    JStarkSqlType type() default JStarkSqlType.MYSQL;


    String jdbcUrl();


    String driverClass();

    String username();

    String password();

}
