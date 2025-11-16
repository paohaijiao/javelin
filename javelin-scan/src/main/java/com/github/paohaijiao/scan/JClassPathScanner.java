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
package com.github.paohaijiao.scan;

import com.github.paohaijiao.anno.JComponent;
import com.github.paohaijiao.anno.JRepository;
import com.github.paohaijiao.anno.JRule;
import com.github.paohaijiao.anno.JService;
import com.github.paohaijiao.model.JBeanDefinitionModel;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class JClassPathScanner {
    private static final Class<?>[] BEAN_ANNOTATIONS = {
            JComponent.class, JService.class, JRepository.class, JRule.class
    };

    public static List<JBeanDefinitionModel> scan(String basePackage) throws IOException, ClassNotFoundException {
        List<JBeanDefinitionModel> beanDefinitions = new ArrayList<>();
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());
            scanDirectory(file, basePackage, beanDefinitions);
        }

        return beanDefinitions;
    }

    private static void scanDirectory(File directory, String packageName, List<JBeanDefinitionModel> beanDefinitions)
            throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), beanDefinitions);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                for (Class annotation : BEAN_ANNOTATIONS) {
                    if (clazz.isAnnotationPresent(annotation)) {
                        JBeanDefinitionModel bd = new JBeanDefinitionModel(clazz);
                        String beanName = getBeanName(clazz, annotation);
                        bd.setSingleton(true);
                        beanDefinitions.add(bd);
                        break;
                    }
                }
            }
        }
    }

    private static String getBeanName(Class<?> clazz, Class<? extends Annotation> annotation) {
        String value = "";
        if (clazz.isAnnotationPresent(JComponent.class)) {
            value = clazz.getAnnotation(JComponent.class).value();
        } else if (clazz.isAnnotationPresent(JService.class)) {
            value = clazz.getAnnotation(JService.class).value();
        } else if (clazz.isAnnotationPresent(JRepository.class)) {
            value = clazz.getAnnotation(JRepository.class).value();
        } else if (clazz.isAnnotationPresent(JRule.class)) {
            value = clazz.getAnnotation(JRule.class).value();
        }
        return value.isEmpty() ? toLowerFirstCase(clazz.getSimpleName()) : value;
    }

    private static String toLowerFirstCase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
