package org.paohaijiao.jstark.reader.impl;

import org.paohaijiao.jstark.reader.JResourceReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

public class JResourceYamlReader <T> implements JResourceReader<T> {
    @Override
    public T parse(InputStream inputStream, Class<T> targetClass) throws IOException {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(targetClass, loaderOptions);
        Yaml yaml = new Yaml(constructor);
        return yaml.load(inputStream);
    }
}
