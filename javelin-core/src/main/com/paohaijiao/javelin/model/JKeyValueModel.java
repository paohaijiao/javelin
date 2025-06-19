package com.paohaijiao.javelin.model;

import lombok.Data;

@Data
public class JKeyValueModel <T>{

    private String key;

    private T value;
}
