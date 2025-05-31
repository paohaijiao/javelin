package org.paohaijiao.jstark.test;

import lombok.Data;
import org.paohaijiao.jstark.anno.JColumn;
import org.paohaijiao.jstark.anno.JTable;

@JTable("user")
@Data
public class JUser {
    @JColumn(id = true)
    private Long id;

    @JColumn("username")
    private String name;

    private Integer age;
}
