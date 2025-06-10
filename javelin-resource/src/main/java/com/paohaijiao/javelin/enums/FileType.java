package com.paohaijiao.javelin.enums;

import com.paohaijiao.javelin.reader.impl.JResourcePropertiesReader;
import com.paohaijiao.javelin.reader.impl.JResourceYamlReader;
import lombok.Getter;
@Getter
public enum FileType {
    YAML("yaml", JResourceYamlReader.class), PROPERTIES("properties", JResourcePropertiesReader.class);
    private String code;
    private Class clazz;

    private FileType(String code,Class clazz){
        this.code = code;
        this.clazz = clazz;
    }

}
