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
package com.github.paohaijiao.image.impl;

import com.github.paohaijiao.base64.JBase64Provider;
import com.github.paohaijiao.image.JBaseImageProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * packageName com.github.paohaijiao.image
 *
 * @author Martin
 * @version 1.0.0
 * @className JLocalFileImageProvider
 * @date 2025/6/28
 * @description
 */
public class JBase64ImageProvider extends JBaseImageProvider {
    private final String base64;

    public JBase64ImageProvider(String base64) {
        this.base64 = base64;
    }

    @Override
    public byte[] loadImage() throws IOException {
        return JBase64Provider.decodeByBase64String(base64);
    }

    public BufferedImage loadAsBufferedImage() throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(this.loadImage())) {
            return ImageIO.read(bis);
        }
    }
}
