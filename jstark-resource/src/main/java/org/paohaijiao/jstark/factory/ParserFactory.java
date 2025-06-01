package org.paohaijiao.jstark.factory;

import org.paohaijiao.jstark.enums.FileType;
import org.paohaijiao.jstark.reader.JResourceReader;
import org.paohaijiao.jstark.reader.impl.JResourcePropertiesReader;
import org.paohaijiao.jstark.reader.impl.JResourceYamlReader;

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
