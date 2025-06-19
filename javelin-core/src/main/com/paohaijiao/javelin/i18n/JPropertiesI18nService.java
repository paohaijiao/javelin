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
package com.paohaijiao.javelin.i18n;

import com.paohaijiao.javelin.exception.JI18NException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * packageName com.paohaijiao.javelin.i18n
 *
 * @author Martin
 * @version 1.0.0
 * @className JPropertiesI18nService
 * @date 2025/6/19
 * @description
 */
public class JPropertiesI18nService implements JI18nService {
    private final Properties properties;
    private final String language;

    public JPropertiesI18nService(String language) {
        this.language = language;
        this.properties = new Properties();
        String fileName = "i18n/messages_" + language + ".properties";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            if (inputStream == null) {
                throw new JI18NException("Resource file not found: " + fileName);
            }
            properties.load(reader);
        } catch (IOException e) {
            throw new JI18NException("Error loading properties file: " + fileName);
        }
    }

    @Override
    public String getMessage(String key) {
        return properties.getProperty(key, "[" + key + "]");
    }

    @Override
    public String getMessage(String key, Object... args) {
        String pattern = getMessage(key);
        return MessageFormat.format(pattern, args);
    }

    @Override
    public String getLanguage() {
        return language;
    }
}
