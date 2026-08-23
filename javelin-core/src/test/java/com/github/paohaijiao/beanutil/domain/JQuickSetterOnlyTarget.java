package com.github.paohaijiao.beanutil.domain;

public class JQuickSetterOnlyTarget {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name + "-setter";
    }
}
