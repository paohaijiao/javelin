package com.paohaijiao.javelin.scan;

import com.paohaijiao.javelin.anno.JComponent;
import com.paohaijiao.javelin.anno.JRepository;
import com.paohaijiao.javelin.anno.JRule;
import com.paohaijiao.javelin.anno.JService;
import com.paohaijiao.javelin.bean.JBeanDefinition;

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

    public static List<JBeanDefinition> scan(String basePackage) throws IOException, ClassNotFoundException {
        List<JBeanDefinition> beanDefinitions = new ArrayList<>();
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

    private static void scanDirectory(File directory, String packageName, List<JBeanDefinition> beanDefinitions)
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
                        JBeanDefinition bd = new JBeanDefinition(clazz);
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
        }else if (clazz.isAnnotationPresent(JRule.class)) {
            value = clazz.getAnnotation(JRule.class).value();
        }
        return value.isEmpty() ? toLowerFirstCase(clazz.getSimpleName()) : value;
    }

    private static String toLowerFirstCase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
