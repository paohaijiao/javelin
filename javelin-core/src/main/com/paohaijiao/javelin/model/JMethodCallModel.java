package com.paohaijiao.javelin.model;

import com.paohaijiao.javelin.enums.JMethodEnums;
import lombok.Data;

import java.util.List;
@Data
public class JMethodCallModel {

    private JMethodEnums method;

    private List<Object> list;
}
