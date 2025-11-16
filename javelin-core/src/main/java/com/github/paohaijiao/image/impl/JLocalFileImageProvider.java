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

import com.github.paohaijiao.image.JBaseImageProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * packageName com.github.paohaijiao.image
 *
 * @author Martin
 * @version 1.0.0
 * @className JLocalFileImageProvider
 * @date 2025/6/28
 * @description
 */
public class JLocalFileImageProvider extends JBaseImageProvider {
    private final String filePath;

    public JLocalFileImageProvider(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public byte[] loadImage() throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public BufferedImage loadAsBufferedImage() throws IOException {
        return ImageIO.read(new File(filePath));
    }
}
