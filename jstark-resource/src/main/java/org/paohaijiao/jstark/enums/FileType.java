package org.paohaijiao.jstark.enums;

import lombok.Getter;
import org.paohaijiao.jstark.reader.impl.JResourcePropertiesReader;
import org.paohaijiao.jstark.reader.impl.JResourceYamlReader;
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
