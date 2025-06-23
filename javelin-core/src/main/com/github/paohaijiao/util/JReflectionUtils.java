package com.github.paohaijiao.util;

import com.github.paohaijiao.exception.JAssert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class JReflectionUtils {
    /**
     * 创建对象实例
     *
     * @param clazz 类对象
     * @param args  构造参数
     * @param <T>   类型
     * @return 对象实例
     */
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        try {
            if (args == null || args.length == 0) {
                return clazz.newInstance();
            }

            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }

            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException
                 | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("创建实例失败", e);
        }
    }

    /**
     * 调用对象方法
     *
     * @param obj        对象
     * @param methodName 方法名
     * @param args       参数
     * @return 方法返回值
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }

            Method method = getDeclaredMethod(obj.getClass(), methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("调用方法失败", e);
        }
    }

    /**
     * 设置对象字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @param value     字段值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("设置字段值失败", e);
        }
    }

    /**
     * 获取对象字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("获取字段值失败", e);
        }
    }

    /**
     * 获取类中声明的字段（包括父类）
     *
     * @param clazz     类
     * @param fieldName 字段名
     * @return 字段
     */
    private static Field getDeclaredField(Class<?> clazz, String fieldName) {
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
        }
        throw new RuntimeException("未找到字段: " + fieldName);
    }

    /**
     * 获取类中声明的方法（包括父类）
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法
     */
    private static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
            }
        }
        throw new RuntimeException("未找到方法: " + methodName);
    }

    /**
     * 获取类的所有字段（包括父类）
     *
     * @param clazz 类
     * @return 字段列表
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        return fields;
    }

    /**
     * 获取类的所有方法（包括父类）
     *
     * @param clazz 类
     * @return 方法列表
     */
    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            methods.addAll(Arrays.asList(superClass.getDeclaredMethods()));
        }
        return methods;
    }

    /**
     * 判断类是否实现了指定接口
     *
     * @param clazz          类
     * @param interfaceClass 接口类
     * @return 是否实现
     */
    public static boolean isImplementInterface(Class<?> clazz, Class<?> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName() + "不是接口");
        }
        return interfaceClass.isAssignableFrom(clazz);
    }

    /**
     * 获取枚举类的所有枚举常量
     *
     * @param enumClass 枚举类
     * @param <E>       枚举类型
     * @return 枚举常量数组
     */
    public static <E extends Enum<E>> E[] getEnumValues(Class<E> enumClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException(enumClass.getName() + "不是枚举类型");
        }
        return enumClass.getEnumConstants();
    }

    /**
     * 通过名称获取枚举常量
     *
     * @param enumClass 枚举类
     * @param name      枚举常量名
     * @param <E>       枚举类型
     * @return 枚举常量
     */
    public static <E extends Enum<E>> E getEnumByName(Class<E> enumClass, String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, name.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 获取枚举常量的自定义字段值
     *
     * @param enumValue  枚举常量
     * @param fieldName  字段名
     * @return 字段值
     */
    public static Object getEnumFieldValue(Enum<?> enumValue, String fieldName) {
        try {
            Field field = enumValue.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(enumValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("获取枚举字段值失败", e);
        }
    }


    /**
     * 检查类是否带有指定注解
     *
     * @param clazz           类
     * @param annotationClass 注解类
     * @return 是否带有注解
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass);
    }

    /**
     * 获取类上的指定注解
     *
     * @param clazz           类
     * @param annotationClass 注解类
     * @param <A>             注解类型
     * @return 注解实例
     */
    public static <A extends Annotation> A getClassAnnotation(Class<?> clazz, Class<A> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    /**
     * 获取字段上的指定注解
     *
     * @param field          字段
     * @param annotationClass 注解类
     * @param <A>            注解类型
     * @return 注解实例
     */
    public static <A extends Annotation> A getFieldAnnotation(Field field, Class<A> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    /**
     * 获取方法上的指定注解
     *
     * @param method         方法
     * @param annotationClass 注解类
     * @param <A>            注解类型
     * @return 注解实例
     */
    public static <A extends Annotation> A getMethodAnnotation(Method method, Class<A> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    /**
     * 获取带有指定注解的所有字段
     *
     * @param clazz          类
     * @param annotationClass 注解类
     * @return 字段列表
     */
    public static List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .collect(Collectors.toList());
    }

    /**
     * 获取带有指定注解的所有方法
     *
     * @param clazz          类
     * @param annotationClass 注解类
     * @return 方法列表
     */
    public static List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .collect(Collectors.toList());
    }

    /**
     * 获取类上的所有注解
     *
     * @param clazz 类
     * @return 注解数组
     */
    public static Annotation[] getClassAnnotations(Class<?> clazz) {
        return clazz.getAnnotations();
    }

    /**
     * 获取注解的属性值
     *
     * @param annotation     注解实例
     * @param attributeName 属性名
     * @return 属性值
     */
    public static Object getAnnotationValue(Annotation annotation, String attributeName) {
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName);
            method.setAccessible(true);
            return method.invoke(annotation);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("获取注解属性值失败", e);
        }
    }


    /**
     * 获取枚举常量上的注解
     *
     * @param enumValue      枚举常量
     * @param annotationClass 注解类
     * @param <A>            注解类型
     * @return 注解实例
     */
    public static <A extends Annotation> A getEnumAnnotation(Enum<?> enumValue, Class<A> annotationClass) {
        try {
            Field field = enumValue.getClass().getDeclaredField(enumValue.name());
            return field.getAnnotation(annotationClass);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("获取枚举注解失败", e);
        }
    }

    /**
     * 扫描包中带有指定注解的类
     *
     * @param packageName    包名
     * @param annotationClass 注解类
     * @return 类集合
     */
    public static Set<Class<?>> findClassesWithAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classes = new HashSet<>();
        return classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(annotationClass))
                .collect(Collectors.toSet());
    }
    /**
     * 根据包名和类名加载类并创建实例
     * @param packageName 包名
     * @param className 类名
     * @param constructorArgs 构造参数
     * @return 对象实例
     */
    public static Object createInstance(String packageName, String className, Object... constructorArgs) {
        try {
            Class<?> clazz = Class.forName(packageName + "." + className);
            return JReflectionUtils.newInstance(clazz, constructorArgs);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("类未找到: " + packageName + "." + className, e);
        }
    }
    /**
     * 动态调用方法
     * @param packageName 包名
     * @param className 类名
     * @param methodName 方法名
     * @param methodArgs 方法参数
     * @param constructorArgs 构造参数(用于创建实例)
     * @return 方法调用结果
     */
    public static Object invokeMethod(String packageName, String className,
                                      String methodName, Object[] methodArgs,
                                      Object... constructorArgs) {
        // 创建实例
        Object instance = createInstance(packageName, className, constructorArgs);
        // 调用方法
        return JReflectionUtils.invokeMethod(instance, methodName, methodArgs);
    }
    public static Object preciseInvokeMethod(String packageName, String className,
                                             String methodName, Object[] methodArgs,
                                             Class<?>[] parameterTypes,
                                             Object... constructorArgs) {
        try {
            Class<?> clazz = Class.forName(packageName + "." + className);
            Object instance = constructorArgs != null && constructorArgs.length > 0
                    ? JReflectionUtils.newInstance(clazz, constructorArgs)
                    : clazz.newInstance();

            Method method = JReflectionUtils.getDeclaredMethod(clazz, methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(instance, methodArgs);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("方法调用失败", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPackageName(String fullClassName) {
        if (fullClassName == null || fullClassName.isEmpty()) {
            throw new IllegalArgumentException("类名不能为空");
        }
        int lastDotIndex = fullClassName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return null;
        }
        String packageName = fullClassName.substring(0, lastDotIndex);
        return packageName;
    }
    public static String getClassName(String fullClassName) {
        if (fullClassName == null || fullClassName.isEmpty()) {
            throw new IllegalArgumentException("类名不能为空");
        }
        int lastDotIndex = fullClassName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fullClassName;
        }

        String className = fullClassName.substring(lastDotIndex + 1);
        return className;
    }



    public static void main(String[] args) {
        Object result = invokeMethod("com.example", "MyClass", "myMethod", null);
        Object[] methodArgs = new Object[]{"param1", 123};
        Object[] constructorArgs = new Object[]{"initValue"};
        Object result1 = invokeMethod("com.example", "MyClass", "myMethod", methodArgs, constructorArgs);
        Object[] methodArgs1 = new Object[]{"param1", 123};
        Class<?>[] paramTypes = new Class<?>[]{String.class, int.class};
        Object result2 = preciseInvokeMethod("com.example", "MyClass", "processData",
                methodArgs, paramTypes);
    }
    public static Map<String, Object> getFieldAndFieldValueByObject(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }
        Map<String, Object> fieldMap = new HashMap<>();
        List<Field> fields = JReflectionUtils.getAllFields(obj.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = JReflectionUtils.getFieldValue(obj, field.getName());
                fieldMap.put(field.getName(), value);
            } catch (Exception e) {
                e.printStackTrace();
                JAssert.throwNewException("invalid get field and field Values");
            }
        }
        return fieldMap;
    }
}
