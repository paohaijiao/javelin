package com.paohaijiao.javelin.test;

import com.paohaijiao.javelin.anno.JColumn;
import com.paohaijiao.javelin.anno.JTable;
import lombok.Data;

@JTable("user")
@Data
public class JUser {
    @JColumn(id = true)
    private Long id;

    @JColumn("username")
    private String name;

    private Integer age;
}
