package com.github.paohaijiao.beanutil.domain;

import lombok.Data;

import java.util.List;
@Data
public class JQuickTargetUser {
    private Long id;

    private String name;

    private Integer age;

    private String email;

    private Boolean active;

    private Double score;

    private JQuickAddress address;

    private List<String> tags;

    private String extraField; // 额外字段，源对象没有

}
