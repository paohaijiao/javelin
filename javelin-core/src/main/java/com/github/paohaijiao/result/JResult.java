/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.result;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.io.Reader;

/**
 * JResult - 统一的响应结果封装类
 * 支持普通数据（字符串、字节数组）和文件数据（流、源）
 *
 * @author Martin
 * @version 1.0.0
 * @date 2025/6/28
 */
@Data
@Builder
public class JResult {

    /**
     * 字符串数据（普通文本响应）
     */
    private String string;

    /**
     * 字节数组数据（二进制数据）
     */
    private byte[] bytes;

    /**
     * 字节流（适用于大文件/流式数据）
     */
    private InputStream byteStream;

    /**
     * 字符流（适用于文本大文件）
     */
    private Reader charStream;


    /**
     * 数据格式标识（如：json、xml、text、binary等，可选）
     */
    private String dataType;

    /**
     * 获取字符串数据
     */
    public String getString() {
        if (string != null) {
            return string;
        }
        if (bytes != null) {
            return new String(bytes);
        }
        return null;
    }

    /**
     * 获取字节数组数据
     */
    public byte[] getBytes() {
        if (bytes != null) {
            return bytes;
        }
        if (string != null) {
            return string.getBytes();
        }
        return null;
    }

    /**
     * 获取字节流
     */
    public InputStream getByteStream() {
        return byteStream;
    }

    /**
     * 获取字符流
     */
    public Reader getCharStream() {
        return charStream;
    }


    /**
     * 判断是否为JSON数据
     */
    public boolean isJson() {
        return "json".equalsIgnoreCase(dataType) ||
                (string != null && string.trim().startsWith("{") && string.trim().endsWith("}"));
    }

    /**
     * 判断是否为XML数据
     */
    public boolean isXml() {
        return "xml".equalsIgnoreCase(dataType) ||
                (string != null && string.trim().startsWith("<") && string.trim().endsWith(">"));
    }

    /**
     * 判断是否有流数据
     */
    public boolean hasStream() {
        return byteStream != null || charStream != null;
    }

    /**
     * 判断是否有内存数据
     */
    public boolean hasMemoryData() {
        return string != null || bytes != null;
    }

    /**
     * 获取数据大小（如果是流数据则返回-1）
     */
    public long getDataSize() {
        if (bytes != null) {
            return bytes.length;
        }
        if (string != null) {
            return string.length();
        }
        return -1;
    }

    /**
     * 构建器扩展方法
     */
    public static class JResultBuilder {

        /**
         * 设置JSON字符串
         */
        public JResultBuilder json(String json) {
            this.string = json;
            this.dataType = "json";
            return this;
        }

        /**
         * 设置XML字符串
         */
        public JResultBuilder xml(String xml) {
            this.string = xml;
            this.dataType = "xml";
            return this;
        }

        /**
         * 设置文本字符串
         */
        public JResultBuilder text(String text) {
            this.string = text;
            this.dataType = "text";
            return this;
        }

        /**
         * 设置字节数组数据
         */
        public JResultBuilder bytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        /**
         * 设置字节流（文件数据）
         */
        public JResultBuilder byteStream(InputStream byteStream) {
            this.byteStream = byteStream;
            return this;
        }

        /**
         * 设置字符流（文件数据）
         */
        public JResultBuilder charStream(Reader charStream) {
            this.charStream = charStream;
            return this;
        }

        /**
         * 设置数据类型
         */
        public JResultBuilder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }
    }
}
