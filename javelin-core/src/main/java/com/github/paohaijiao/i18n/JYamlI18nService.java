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
package com.github.paohaijiao.i18n;

import com.github.paohaijiao.exception.JI18NException;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * packageName com.paohaijiao.javelin.i18n
 *
 * @author Martin
 * @version 1.0.0
 * @className JYamlI18nService
 * @date 2025/6/19
 * @description
 */
public class JYamlI18nService implements JI18nService{

    private final Map<String, Object> messages;

    private final String language;

    @SuppressWarnings("unchecked")
    public JYamlI18nService(String language) {
        this.language = language;
        String fileName = "i18n/messages_" + language + ".yml";

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new JI18NException("Resource file not found: " + fileName);
            }
            Yaml yaml = new Yaml();
            this.messages = (Map<String, Object>) yaml.load(inputStream);
        } catch (Exception e) {
            throw new JI18NException("Error loading YAML file: " + fileName);
        }
    }

    @Override
    public String getMessage(String key) {
        Object value = messages.get(key);
        return value != null ? value.toString() : "[" + key + "]";
    }

    @Override
    public String getMessage(String key, Object... args) {
        String pattern = getMessage(key);
        return String.format(pattern, args);
    }

    @Override
    public String getLanguage() {
        return language;
    }
}
