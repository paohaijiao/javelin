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
package com.github.paohaijiao.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * packageName com.github.paohaijiao.image
 *
 * @author Martin
 * @version 1.0.0
 * @className JImageProcessor
 * @date 2025/6/28
 * @description
 */
public abstract class JBaseImageProvider implements JImageProvider {

    public abstract byte[] loadImage() throws IOException;

    /**
     * @param image
     * @param formatName
     * @return
     * @throws IOException
     */
    public byte[] toByteArray(BufferedImage image, String formatName) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     *
     * @param source
     * @return
     */

}
