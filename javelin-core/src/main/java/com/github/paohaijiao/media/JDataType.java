package com.github.paohaijiao.media;

public enum JDataType {
    STRING("string"),

    /**
     * 字节数组类型 - 适用于二进制数据、图片、PDF等
     */
    BYTES("bytes"),

    /**
     * 字节流类型 - 适用于大文件流式传输
     */
    BYTE_STREAM("byteStream"),

    /**
     * 字符流类型 - 适用于大文本文件流式传输
     */
    CHAR_STREAM("charStream");

    private final String dataType;

    JDataType(String dataType) {
        this.dataType = dataType;
    }
}
