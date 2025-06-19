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
package com.paohaijiao.javelin.i18n.factory;

import com.paohaijiao.javelin.enums.JResourceType;
import com.paohaijiao.javelin.exception.JI18NException;
import com.paohaijiao.javelin.i18n.JI18nService;
import com.paohaijiao.javelin.i18n.JPropertiesI18nService;
import com.paohaijiao.javelin.i18n.JYamlI18nService;

/**
 * packageName com.paohaijiao.javelin.exception.factory
 *
 * @author Martin
 * @version 1.0.0
 * @className JI18nFactory
 * @date 2025/6/19
 * @description
 */
public class JI18nFactory {
    public static JI18nService getI18nService(String language, JResourceType type) {
        if (language == null || language.trim().isEmpty()) {
            throw new JI18NException("Language cannot be null or empty");
        }

        switch (type) {
            case PROPERTIES:
                return new JPropertiesI18nService(language);
            case YAML:
                return new JYamlI18nService(language);
            default:
                throw new JI18NException("Unsupported resource type: " + type);
        }
    }

    public static JI18nService getI18nService(String language) {
        return getI18nService(language, JResourceType.PROPERTIES);
    }
}
