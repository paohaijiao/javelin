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
package com.github.paohaijiao.reader;

import java.io.IOException;
import java.io.InputStream;

/**
 * File Parse to parse the File
 * @param <T>
 */
public interface JResourceReader<T>  {
    /**
     *
     * @param inputStream
     * @param targetClass
     * @return
     * @throws IOException
     */
    T parse(InputStream inputStream, Class<T> targetClass) throws IOException;

    /**
     *
     * @param inputStream
     * @param targetClass
     * @param key
     * @return
     * @throws IOException
     */
    T parse(InputStream inputStream, Class<T> targetClass,String key) throws IOException;

    /**
     *
     * @param filePath
     * @param targetClass
     * @return
     * @throws IOException
     */
    default T parse(String filePath, Class<T> targetClass) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IOException("File not found: " + filePath);
            }
            return parse(is, targetClass);
        }
    }

    /**
     *
     * @param filePath
     * @param targetClass
     * @param key
     * @return
     * @throws IOException
     */
    default T parse(String filePath, Class<T> targetClass,String key) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IOException("File not found: " + filePath);
            }
            return parse(is, targetClass,key);
        }
    }

}
