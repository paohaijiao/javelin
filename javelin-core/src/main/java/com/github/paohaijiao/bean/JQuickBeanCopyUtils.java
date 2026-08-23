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
package com.github.paohaijiao.bean;

import com.github.paohaijiao.statement.asm.JQuickJavaBeanIntrospection;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 通用 Bean 拷贝工具类。
 *
 * <p>默认拷贝路径使用 jquick-asm 生成 BeanCopier；忽略字段和 mergeNonNull
 * 继续保留本地反射逻辑，以维持现有语义。</p>
 *
 * @author YourName
 */
public class JQuickBeanCopyUtils {

    private static final Map<BeanCopyKey, BeanCopier> COPIER_CACHE = new ConcurrentHashMap<>();

    /**
     * 将源对象的属性值复制到目标对象（浅拷贝）
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copy(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        copyWithAsm(source, target);
    }

    /**
     * 将源对象的属性值复制到目标对象，并支持忽略指定字段
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreProperties 忽略的属性名列表
     */
    public static void copy(Object source, Object target, String... ignoreProperties) {
        if (source == null || target == null) {
            return;
        }
        Set<String> ignoreSet = toIgnoreSet(ignoreProperties);
        if (ignoreSet.isEmpty()) {
            copyWithAsm(source, target);
            return;
        }
        copyProperties(source, target, ignoreSet, true);
    }

    /**
     * 根据源对象和目标 Class，创建一个新的目标对象并拷贝属性
     *
     * @param source     源对象
     * @param targetType 目标类型
     * @param <T>        目标类型
     * @return 新的目标对象，源为 null 则返回 null
     */
    public static <T> T copy(Object source, Class<T> targetType) {
        return copyToNew(source, targetType, null);
    }

    /**
     * 根据源对象和目标 Class，创建新对象，并支持忽略指定属性
     *
     * @param source           源对象
     * @param targetType       目标类型
     * @param ignoreProperties 忽略的属性名列表
     * @param <T>              目标类型
     * @return 新的目标对象
     */
    public static <T> T copy(Object source, Class<T> targetType, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        Set<String> ignoreSet = toIgnoreSet(ignoreProperties);
        return copyToNew(source, targetType, ignoreSet);
    }

    private static <T> T copyToNew(Object source, Class<T> targetType, Set<String> ignoreProperties) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetType.getDeclaredConstructor().newInstance();
            if (ignoreProperties == null || ignoreProperties.isEmpty()) {
                copyWithAsm(source, target);
            } else {
                copyProperties(source, target, ignoreProperties, true);
            }
            return target;
        } catch (Exception e) {
            throw new RuntimeException("创建 " + targetType.getName() + " 实例失败", e);
        }
    }

    public static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetType) {
        return copyToList(sourceList, targetType, (Set<String>) null);
    }

    public static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetType, String... ignoreProperties) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }
        Set<String> ignoreSet = toIgnoreSet(ignoreProperties);
        return copyToList(sourceList, targetType, ignoreSet);
    }

    public static <S, T> List<T> copyToList(Collection<S> sourceList, Supplier<T> targetFactory) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<T> resultList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            if (source == null) {
                resultList.add(null);
                continue;
            }
            T target = targetFactory.get();
            copyWithAsm(source, target);
            resultList.add(target);
        }
        return resultList;
    }

    private static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetType, Set<String> ignoreProperties) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<T> resultList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            if (source == null) {
                resultList.add(null);
                continue;
            }
            resultList.add(copyToNew(source, targetType, ignoreProperties));
        }
        return resultList;
    }

    public static void mergeNonNull(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        copyProperties(source, target, null, false);
    }

    private static void copyWithAsm(Object source, Object target) {
        BeanCopyKey key = new BeanCopyKey(source.getClass(), target.getClass());
        BeanCopier copier = COPIER_CACHE.computeIfAbsent(key, BeanCopierFactory::create);
        copier.copy(source, target);
    }

    private static Set<String> toIgnoreSet(String... ignoreProperties) {
        if (ignoreProperties == null || ignoreProperties.length == 0) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(ignoreProperties));
    }

    private static void copyProperties(Object source, Object target, Set<String> ignoreSet, boolean copyNullValues) {
        if (source == null || target == null) {
            return;
        }
        Map<String, Field> targetFieldMap = getAllFields(target.getClass());
        for (JQuickJavaBeanIntrospection.PropertyInfo propertyInfo : JQuickJavaBeanIntrospection.resolve(source.getClass())) {
            String propertyName = propertyInfo.propertyName;
            if (ignoreSet != null && ignoreSet.contains(propertyName)) {
                continue;
            }
            Field targetField = targetFieldMap.get(propertyName);
            if (targetField == null) {
                continue;
            }
            if (Modifier.isStatic(targetField.getModifiers()) || Modifier.isFinal(targetField.getModifiers())) {
                continue;
            }
            targetField.setAccessible(true);
            try {
                Object value = propertyInfo.method.invoke(source);
                if (!copyNullValues && value == null) {
                    continue;
                }
                if (value != null && !targetField.getType().isAssignableFrom(value.getClass())) {
                    value = convertIfNeeded(value, targetField.getType());
                }
                targetField.set(target, value);
            } catch (Exception ignored) {
            }
        }
    }

    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!fieldMap.containsKey(field.getName())) {
                    fieldMap.put(field.getName(), field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fieldMap;
    }

    private static Object convertIfNeeded(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        Class<?> sourceType = value.getClass();
        if (targetType.isPrimitive() || sourceType.isPrimitive()) {
            if (targetType == Integer.class || targetType == int.class) {
                return ((Number) value).intValue();
            }
            if (targetType == Long.class || targetType == long.class) {
                return ((Number) value).longValue();
            }
            if (targetType == Double.class || targetType == double.class) {
                return ((Number) value).doubleValue();
            }
            if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.valueOf(value.toString());
            }
        }
        return value;
    }

    public interface BeanCopier {
        void copy(Object source, Object target);
    }

    private static final class BeanCopierFactory {

        private static BeanCopier create(BeanCopyKey key) {
            try {
                return createAsmCopier(key.sourceClass, key.targetClass);
            } catch (Exception e) {
                return new ReflectionBeanCopier(key.sourceClass, key.targetClass);
            }
        }

        private static BeanCopier createAsmCopier(Class<?> sourceClass, Class<?> targetClass) throws Exception {
            CopyPlan plan = CopyPlan.build(sourceClass, targetClass);
            if (plan.steps.isEmpty()) {
                return new ReflectionBeanCopier(sourceClass, targetClass);
            }
            String sourceInternal = Type.getInternalName(sourceClass);
            String targetInternal = Type.getInternalName(targetClass);
            String generatedName = "com/github/paohaijiao/bean/generated/BeanCopier_"
                    + sourceClass.getSimpleName() + "_" + targetClass.getSimpleName() + "_"
                    + Math.abs(System.identityHashCode(sourceClass) * 31 + System.identityHashCode(targetClass));
            String dotName = generatedName.replace('/', '.');
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cw.visit(Opcodes.V1_8,
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
                    generatedName,
                    null,
                    "java/lang/Object",
                    new String[]{Type.getInternalName(BeanCopier.class)});
            MethodVisitor init = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            init.visitCode();
            init.visitVarInsn(Opcodes.ALOAD, 0);
            init.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            init.visitInsn(Opcodes.RETURN);
            init.visitMaxs(0, 0);
            init.visitEnd();
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                    "copy",
                    "(Ljava/lang/Object;Ljava/lang/Object;)V",
                    null,
                    null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitTypeInsn(Opcodes.CHECKCAST, sourceInternal);
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitTypeInsn(Opcodes.CHECKCAST, targetInternal);
            mv.visitVarInsn(Opcodes.ASTORE, 4);
            for (CopyStep step : plan.steps) {
                emitCopyStep(mv, sourceInternal, targetInternal, step);
            }
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            cw.visitEnd();
            byte[] bytes = cw.toByteArray();
            ByteArrayClassLoader loader = new ByteArrayClassLoader();
            loader.defineClass(dotName, bytes);
            Class<?> copierClass = loader.loadClass(dotName);
            return (BeanCopier) copierClass.getDeclaredConstructor().newInstance();
        }

        private static void emitCopyStep(MethodVisitor mv, String sourceInternal, String targetInternal, CopyStep step) {
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, sourceInternal, step.getterName, step.getterDesc, false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, targetInternal, step.setterName, step.setterDesc, false);
        }
    }

    private static final class ReflectionBeanCopier implements BeanCopier {

        private final CopyPlan plan;

        private ReflectionBeanCopier(Class<?> sourceClass, Class<?> targetClass) {
            this.plan = CopyPlan.build(sourceClass, targetClass);
        }

        @Override
        public void copy(Object source, Object target) {
            for (CopyStep step : plan.steps) {
                try {
                    Object value = step.getter.invoke(source);
                    if (value != null && !step.parameterType.isAssignableFrom(value.getClass())) {
                        value = convertIfNeeded(value, step.parameterType);
                    }
                    step.setter.invoke(target, value);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static final class CopyPlan {
        private final List<CopyStep> steps;

        private CopyPlan(List<CopyStep> steps) {
            this.steps = steps;
        }

        private static CopyPlan build(Class<?> sourceClass, Class<?> targetClass) {
            Map<String, Method> setterMap = resolveSetterMap(targetClass);
            List<CopyStep> steps = new ArrayList<>();
            for (JQuickJavaBeanIntrospection.PropertyInfo propertyInfo : JQuickJavaBeanIntrospection.resolvePublic(sourceClass)) {
                Method setter = setterMap.get(propertyInfo.propertyName);
                if (setter == null) {
                    continue;
                }
                Class<?> parameterType = setter.getParameterTypes()[0];
                if (!isCompatible(propertyInfo.returnType, parameterType)) {
                    continue;
                }
                steps.add(new CopyStep(propertyInfo.method, setter));
            }
            return new CopyPlan(steps);
        }

        private static Map<String, Method> resolveSetterMap(Class<?> targetClass) {
            Map<String, Method> setterMap = new LinkedHashMap<>();
            for (Class<?> current = targetClass; current != null && current != Object.class; current = current.getSuperclass()) {
                for (Method method : current.getDeclaredMethods()) {
                    if (!Modifier.isPublic(method.getModifiers())) {
                        continue;
                    }
                    if (method.getParameterCount() != 1) {
                        continue;
                    }
                    if (!method.getName().startsWith("set") || method.getName().length() <= 3) {
                        continue;
                    }
                    String propertyName = extractSetterPropertyName(method.getName());
                    if (!setterMap.containsKey(propertyName)) {
                        setterMap.put(propertyName, method);
                    }
                }
            }
            return setterMap;
        }

        private static boolean isCompatible(Class<?> sourceType, Class<?> targetType) {
            if (targetType.isAssignableFrom(sourceType)) {
                return true;
            }
            return primitiveToWrapper(sourceType) == primitiveToWrapper(targetType)
                    || (Number.class.isAssignableFrom(primitiveToWrapper(sourceType))
                    && Number.class.isAssignableFrom(primitiveToWrapper(targetType)))
                    || (primitiveToWrapper(targetType) == Boolean.class && primitiveToWrapper(sourceType) == Boolean.class);
        }

        private static String extractSetterPropertyName(String methodName) {
            String raw = methodName.substring(3);
            if (raw.length() >= 2 && Character.isUpperCase(raw.charAt(0)) && Character.isUpperCase(raw.charAt(1))) {
                return raw;
            }
            return Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
        }

        private static Class<?> primitiveToWrapper(Class<?> type) {
            if (!type.isPrimitive()) {
                return type;
            }
            if (type == int.class) {
                return Integer.class;
            }
            if (type == long.class) {
                return Long.class;
            }
            if (type == double.class) {
                return Double.class;
            }
            if (type == float.class) {
                return Float.class;
            }
            if (type == short.class) {
                return Short.class;
            }
            if (type == byte.class) {
                return Byte.class;
            }
            if (type == boolean.class) {
                return Boolean.class;
            }
            if (type == char.class) {
                return Character.class;
            }
            return type;
        }
    }

    private static final class CopyStep {

        private final Method getter;

        private final Method setter;

        private final String getterName;

        private final String getterDesc;

        private final String setterName;

        private final String setterDesc;

        private final Class<?> parameterType;

        private CopyStep(Method getter, Method setter) {
            this.getter = getter;
            this.setter = setter;
            this.getterName = getter.getName();
            this.getterDesc = Type.getMethodDescriptor(getter);
            this.setterName = setter.getName();
            this.setterDesc = Type.getMethodDescriptor(setter);
            this.parameterType = setter.getParameterTypes()[0];
        }
    }

    private static final class ByteArrayClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException e) {
                return findClass(name);
            }
        }

        private Class<?> defineClass(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    private static final class BeanCopyKey {

        private final Class<?> sourceClass;

        private final Class<?> targetClass;

        private BeanCopyKey(Class<?> sourceClass, Class<?> targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BeanCopyKey)) {
                return false;
            }
            BeanCopyKey that = (BeanCopyKey) o;
            return sourceClass == that.sourceClass && targetClass == that.targetClass;
        }

        @Override
        public int hashCode() {
            int result = System.identityHashCode(sourceClass);
            result = 31 * result + System.identityHashCode(targetClass);
            return result;
        }
    }
}
