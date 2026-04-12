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
package com.github.paohaijiao.media;

/**
 * packageName com.github.paohaijiao.media
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/4/12
 */

import lombok.Data;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于 SPI 的动态媒体类型管理器
 * 支持优先级排序和插件化扩展
 */
@Data
public class MediaTypeInfo {

    private final String code;

    private final Charset charset;

    private final List<String> extensions;

    private JDataType dataType;

    private boolean streamSet = false;

    public MediaTypeInfo(String code, Charset charset, JDataType dataType, List<String> extensions) {
        this.code = code;
        this.charset = charset;
        this.dataType = dataType;
        this.streamSet = true;
        this.extensions = extensions != null ? new ArrayList<>(extensions) : new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<String> getExtensions() {
        return new ArrayList<>(extensions);
    }

    @Override
    public String toString() {
        return String.format("MediaTypeInfo{code='%s', charset=%s, dataType=%s, extensions=%s}", code, charset != null ? charset.name() : "null", dataType, extensions);
    }
}