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

import lombok.Data;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 媒体类型定义
 */
@Data
public class MediaTypeDefinition {

    private final String code;

    private final Charset charset;

    private final JDataType dataType;

    private final List<String> extensions;

    public MediaTypeDefinition(String code, Charset charset, JDataType dataType, List<String> extensions) {
        this.code = code;
        this.charset = charset;
        this.dataType = dataType;
        this.extensions = extensions != null ? new ArrayList<>(extensions) : new ArrayList<>();
    }

    public MediaTypeDefinition(String code, JDataType dataType, List<String> extensions) {
        this(code, null, dataType, extensions);
    }

}