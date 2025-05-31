package org.paohaijiao.jstark.scan;

import org.paohaijiao.jstark.anno.Component;
import org.paohaijiao.jstark.anno.Repository;
import org.paohaijiao.jstark.anno.Service;
import org.paohaijiao.jstark.context.bean.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassPathScanner {
    private static final Class<?>[] BEAN_ANNOTATIONS = {
            Component.class, Service.class, Repository.class
    };

    public static List<BeanDefinition> scan(String basePackage) throws IOException, ClassNotFoundException {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
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

    private static void scanDirectory(File directory, String packageName, List<BeanDefinition> beanDefinitions)
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
                        BeanDefinition bd = new BeanDefinition(clazz);
                        // 使用注解的value作为beanName，如果没有则使用类名首字母小写
                        String beanName = getBeanName(clazz, annotation);
                        bd.setSingleton(true); // 默认单例
                        beanDefinitions.add(bd);
                        break;
                    }
                }
            }
        }
    }

    private static String getBeanName(Class<?> clazz, Class<? extends Annotation> annotation) {
        String value = "";
        if (clazz.isAnnotationPresent(Component.class)) {
            value = clazz.getAnnotation(Component.class).value();
        } else if (clazz.isAnnotationPresent(Service.class)) {
            value = clazz.getAnnotation(Service.class).value();
        } else if (clazz.isAnnotationPresent(Repository.class)) {
            value = clazz.getAnnotation(Repository.class).value();
        }
        return value.isEmpty() ? toLowerFirstCase(clazz.getSimpleName()) : value;
    }

    private static String toLowerFirstCase(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
