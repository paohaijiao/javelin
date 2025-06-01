package com.paohaijiao.javelin.test;

import lombok.Data;
import com.paohaijiao.javelin.anno.JColumn;
import com.paohaijiao.javelin.anno.JTable;

@JTable("user")
@Data
public class JUser {
    @JColumn(id = true)
    private Long id;

    @JColumn("username")
    private String name;

    private Integer age;
}
