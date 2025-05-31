package org.paohaijiao.jstark.bean;

import lombok.Data;

@Data
public class JCondition {
    private final String column;
    private final String operator;
    private final Object value;
}
