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
package com.github.paohaijiao.reader.impl;

import com.github.paohaijiao.reader.JResourceBaseReader;
import com.github.paohaijiao.reader.JResourceReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class JResourceYamlReader <T> extends JResourceBaseReader implements JResourceReader<T> {
    @Override
    public T parse(InputStream inputStream, Class<T> targetClass) throws IOException {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(targetClass, loaderOptions);
        Yaml yaml = new Yaml(constructor);
        return yaml.load(inputStream);
    }

    @Override
    public T parse(InputStream inputStream, Class<T> targetClass, String key) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(inputStream);
        if (yamlMap == null) {
            throw new IOException("YAML file is empty");
        }
        Object configPart = extractConfigPart(yamlMap, key);
        if (configPart == null) {
            throw new IOException("Config part with key '" + key + "' not found");
        }
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(targetClass, loaderOptions);
        Yaml targetYaml = new Yaml(constructor);
        String filteredYaml = targetYaml.dump(configPart);
        return targetYaml.loadAs(filteredYaml, targetClass);
    }
    private Object extractConfigPart(Map<String, Object> yamlMap, String key) {
        String[] keyParts = key.split("\\.");
        Object current = yamlMap;
        for (String part : keyParts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }

        return current;
    }
}
