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
package com.github.paohaijiao.factory;

import com.github.paohaijiao.base64.JBase64Provider;
import com.github.paohaijiao.image.JBaseImageProvider;
import com.github.paohaijiao.image.impl.JBase64ImageProvider;
import com.github.paohaijiao.image.impl.JLocalFileImageProvider;
import com.github.paohaijiao.image.impl.JNetworkImageProvider;

/**
 * packageName com.github.paohaijiao.factory
 *
 * @author Martin
 * @version 1.0.0
 * @className JImageFactory
 * @date 2025/6/28
 * @description
 */
public class JImageFactory {
    public static JBaseImageProvider createProvider(String source) {
        if (source.startsWith("http://") || source.startsWith("https://")) {
            return new JNetworkImageProvider(source);
        } else if (JBase64Provider.isBase64(source)) {
            return new JBase64ImageProvider(source);
        } else {
            return new JLocalFileImageProvider(source);
        }
    }

}
