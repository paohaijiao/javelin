package com.paohaijiao.javelin.enums;

import lombok.Getter;
import com.paohaijiao.javelin.reader.impl.JResourcePropertiesReader;
import com.paohaijiao.javelin.reader.impl.JResourceYamlReader;
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
