package com.paohaijiao.javelin.factory;

import com.paohaijiao.javelin.enums.FileType;
import com.paohaijiao.javelin.reader.JResourceReader;
import com.paohaijiao.javelin.reader.impl.JResourcePropertiesReader;
import com.paohaijiao.javelin.reader.impl.JResourceYamlReader;

public class ParserFactory {
    public static <T> JResourceReader<T> createParser(FileType fileType) {
        switch (fileType) {
            case YAML:
                return new JResourceYamlReader<>();
            case PROPERTIES:
                return new JResourcePropertiesReader<>();
            default:
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }
}
