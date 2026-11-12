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
package com.github.paohaijiao.statement.asm;


import com.github.paohaijiao.date.JDateUtil;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.type.JQuickTypeConverter;
import com.github.paohaijiao.type.JQuickTypeConverterManager;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JQuickRow → Bean 转换器（ASM 字节码生成 + 反射降级）。
 *
 * <p>与 {@link JQuickRowConverterGenerator} 对称，实现 Row → Bean 的零反射转换。
 *
 * <h3>性能对比</h3>
 * <ul>
 *   <li>反射方式：~100-200 ns/条（简单 Bean）</li>
 *   <li>ASM 方式：~10-20 ns/条（接近手写硬编码）</li>
 * </ul>
 */
public final class JQuickRowToBeanConverter {

    /**
     * 转换器缓存：目标类 → Converter 实例
     */
    private static final Map<Class<?>, RowToBeanConverter<?>> CONVERTER_CACHE = new ConcurrentHashMap<>();
    /**
     * ASM 优化阈值：单个 Row 数量 >= 该值启动 ASM，否则用反射降级
     */
    private static final int OPTIMIZATION_THRESHOLD = 10;

    private JQuickRowToBeanConverter() {
    }

    /**
     * 将 JQuickRow 转换为指定类型的 Bean。
     *
     * @param row   数据行
     * @param clazz 目标 Bean 类型
     * @param <T>   Bean 类型
     * @return 转换后的 Bean 实例，失败返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(JQuickRow row, Class<T> clazz) {
        if (row == null || clazz == null) {
            return null;
        }
        if (row.size() < OPTIMIZATION_THRESHOLD) {
            return BeanIntrospector.toBean(row, clazz);
        }
        RowToBeanConverter<T> converter = (RowToBeanConverter<T>) CONVERTER_CACHE.computeIfAbsent(clazz, JQuickRowToBeanConverter::createAsmConverter);
        return converter.convert(row);
    }

    /**
     * 批量转换：利用缓存和预分配提升性能。
     */
    @SuppressWarnings("unchecked")
    public static <T> java.util.List<T> toBeanList(java.util.List<JQuickRow> rows, Class<T> clazz) {
        if (rows == null || rows.isEmpty() || clazz == null) {
            return new java.util.ArrayList<>();
        }
        java.util.List<T> result = new java.util.ArrayList<>(rows.size());
        if (rows.size() < OPTIMIZATION_THRESHOLD) {
            for (JQuickRow row : rows) {
                result.add(BeanIntrospector.toBean(row, clazz));
            }
            return result;
        }
        RowToBeanConverter<T> converter = (RowToBeanConverter<T>) CONVERTER_CACHE.computeIfAbsent(clazz, JQuickRowToBeanConverter::createAsmConverter);
        for (JQuickRow row : rows) {
            if (row != null) {
                result.add(converter.convert(row));
            }
        }
        return result;
    }

    /**
     * 清除缓存（用于单元测试/热部署）。
     */
    public static void clearCache() {
        CONVERTER_CACHE.clear();
    }

    /**
     * 使用 ASM 生成转换器。
     */
    private static <T> RowToBeanConverter<T> createAsmConverter(Class<T> clazz) {
        try {
            JQuickJavaBeanIntrospection.PropertyInfo[] props = JQuickJavaBeanIntrospection.resolve(clazz);
            if (props.length == 0) {
                return new ReflectionRowToBeanConverter<>(clazz);
            }
            Constructor<T> ctor;
            try {
                ctor = clazz.getDeclaredConstructor();
                if (!Modifier.isPublic(ctor.getModifiers())) {
                    ctor.setAccessible(true);
                }
            } catch (NoSuchMethodException e) {
                return new ReflectionRowToBeanConverter<>(clazz);
            }
            String targetInternal = Type.getInternalName(clazz);
            String generatedName = "com/github/paohaijiao/statement/generated/RowToBean_" + clazz.getSimpleName() + "_" + System.identityHashCode(clazz);
            String dotName = generatedName.replace('/', '.');
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cw.visit(Opcodes.V1_8,
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
                    generatedName, null, "java/lang/Object",
                    new String[]{"com/github/paohaijiao/statement/asm/JQuickRowToBeanConverter$RowToBeanConverter"});

            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            MethodVisitor cmv = cw.visitMethod(
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                    "convert",
                    "(Lcom/github/paohaijiao/statement/JQuickRow;)Ljava/lang/Object;",
                    null, null);
            writeConvertBody(cmv, clazz, props, targetInternal, generatedName);
            cmv.visitMaxs(0, 0);
            cmv.visitEnd();

            cw.visitEnd();
            byte[] bytes = cw.toByteArray();

            JQuickRowOptimizer.ByteArrayClassLoader loader = new JQuickRowOptimizer.ByteArrayClassLoader();
            loader.defineClass(dotName, bytes);
            @SuppressWarnings("unchecked")
            Class<RowToBeanConverter<T>> converterClass = (Class<RowToBeanConverter<T>>) loader.loadClass(dotName);
            return converterClass.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            return new ReflectionRowToBeanConverter<>(clazz);
        }
    }

    /**
     * 写入 convert 方法体。
     */
    private static void writeConvertBody(MethodVisitor mv, Class<?> targetClass, JQuickJavaBeanIntrospection.PropertyInfo[] props, String targetInternal, String selfInternal) {
        String rowInternal = "com/github/paohaijiao/statement/JQuickRow";
        String mapInternal = "java/util/Map";
        mv.visitCode();
        // 局部变量表:
        // 0: this, 1: row (JQuickRow), 2: bean (T), 3: map (Map), 4: temp (临时值)
        final int L_THIS = 0, L_ROW = 1, L_BEAN = 2, L_MAP = 3;

        // bean = new targetClass()
        mv.visitTypeInsn(Opcodes.NEW, targetInternal);
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, targetInternal, "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, L_BEAN);

        //  map = row.internalMap()  （直接获取内部 Map，避免 get/entrySet 虚分派）
        mv.visitVarInsn(Opcodes.ALOAD, L_ROW);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, rowInternal, "internalMap",
                "()L" + mapInternal + ";", false);
        mv.visitVarInsn(Opcodes.ASTORE, L_MAP);

        //  对每个属性：从 map 取值，set 到 bean
        for (JQuickJavaBeanIntrospection.PropertyInfo p : props) {
            // 获取 setter 方法（或直接 Field 赋值）
            Method setter = findSetter(targetClass, p.propertyName, p.returnType);
            if (setter == null) {
                // 没有 setter，尝试直接 Field 赋值
                emitFieldSet(mv, targetInternal, p.propertyName, p.returnType, p.primitive);
                continue;
            }

            // 如果有 setter，生成: bean.setXxx((Xxx) map.get("propertyName"))
            emitSetterCall(mv, targetInternal, setter, p);
        }
        //  return bean
        mv.visitVarInsn(Opcodes.ALOAD, L_BEAN);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    /**
     * 通过 Setter 方法赋值。
     */
    private static void emitSetterCall(MethodVisitor mv, String targetInternal, Method setter, JQuickJavaBeanIntrospection.PropertyInfo p) {
        String setterName = setter.getName();
        String setterDesc = Type.getMethodDescriptor(setter);
        boolean isPrimitive = p.primitive;
        Class<?> returnType = p.returnType;
        // bean (栈底)
        mv.visitVarInsn(Opcodes.ALOAD, 2);

        // 从 map 取值: map.get("propertyName")
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitLdcInsn(p.propertyName);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);

        emitCoerce(mv, returnType, isPrimitive);

        // 调用 setter
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, targetInternal, setterName, setterDesc, false);
    }

    /**
     * 直接 Field 赋值（当没有 setter 时使用）。
     */
    private static void emitFieldSet(MethodVisitor mv, String targetInternal, String fieldName, Class<?> fieldType, boolean isPrimitive) {
        // bean (栈底)
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        // 从 map 取值
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitLdcInsn(fieldName);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", "get",
                "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        emitCoerce(mv, fieldType, isPrimitive);
        // PUTFIELD
        String fieldDesc = Type.getDescriptor(fieldType);
        mv.visitFieldInsn(Opcodes.PUTFIELD, targetInternal, fieldName, fieldDesc);
    }

    /** 生成 coerce 调用，栈顶 Object → 目标类型（引用类型 CHECKCAST / 基本类型拆箱）。 */
    private static void emitCoerce(MethodVisitor mv, Class<?> targetType, boolean isPrimitive) {
        Class<?> classConstant = isPrimitive ? box(targetType) : targetType;
        mv.visitLdcInsn(Type.getType(classConstant));
        mv.visitInsn(isPrimitive ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                Type.getInternalName(JQuickRowToBeanConverter.class), "coerce",
                "(Ljava/lang/Object;Ljava/lang/Class;Z)Ljava/lang/Object;", false);
        if (isPrimitive) {
            unboxPrimitive(mv, targetType);
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(targetType));
        }
    }

    /**
     * 基本类型拆箱：将包装类型转换为基本类型。
     */
    private static void unboxPrimitive(MethodVisitor mv, Class<?> type) {
        if (type == int.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        } else if (type == long.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        } else if (type == boolean.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
        } else if (type == double.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        } else if (type == float.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
        } else if (type == short.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
        } else if (type == byte.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
        } else if (type == char.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
        }
    }

    /**
     * 统一类型转换，ASM 路径与反射降级共用。内置规则优先，无法处理时通过 SPI 扩展
     * {@link com.github.paohaijiao.type.JQuickTypeConverter} 兜底。
     */
    public static Object coerce(Object value, Class<?> targetType, boolean primitive) {
        if (targetType == null) {
            return value;
        }
        if (value == null) {
            return primitive ? primitiveDefault(targetType) : null;
        }
        Class<?> type = box(targetType);
        if (type.isInstance(value)) {
            return value;
        }
        if (type == String.class) {
            return value.toString();
        }
        if (type == Boolean.class) {
            if (value instanceof Boolean) {
                return value;
            }
            if (value instanceof Number) {
                return ((Number) value).longValue() != 0L;
            }
            String text = value.toString().trim();
            return "true".equalsIgnoreCase(text) || "1".equals(text) || "y".equalsIgnoreCase(text)
                    || "yes".equalsIgnoreCase(text) || "on".equalsIgnoreCase(text);
        }
        if (type == Character.class) {
            if (value instanceof Number) {
                return (char) ((Number) value).intValue();
            }
            String text = value.toString();
            return text.isEmpty() ? primitiveDefault(char.class) : text.charAt(0);
        }
        if (type == BigDecimal.class) {
            return new BigDecimal(value.toString().trim());
        }
        if (type == BigInteger.class) {
            return new BigDecimal(value.toString().trim()).toBigInteger();
        }
        if (Number.class.isAssignableFrom(type)) {
            BigDecimal number = toBigDecimal(value);
            if (number == null) {
                return convertBySpi(value, type, value);
            }
            if (type == Integer.class) {
                return number.intValue();
            }
            if (type == Long.class) {
                return number.longValue();
            }
            if (type == Double.class) {
                return number.doubleValue();
            }
            if (type == Float.class) {
                return number.floatValue();
            }
            if (type == Short.class) {
                return number.shortValue();
            }
            if (type == Byte.class) {
                return number.byteValue();
            }
            return number;
        }
        if (Date.class.isAssignableFrom(type)) {
            Date date = toDate(value);
            if (date == null) {
                return convertBySpi(value, type, new IllegalArgumentException("Cannot convert [" + value + "] to " + type.getName()));
            }
            return toSqlDate(date, type);
        }
        if (type.isEnum()) {
            String name = value.toString().trim();
            for (Object constant : type.getEnumConstants()) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(name)) {
                    return constant;
                }
            }
            return convertBySpi(value, type, value);
        }
        return convertBySpi(value, type, value);
    }

    /** SPI 兜底，fallback 为 RuntimeException 时抛出。 */
    private static Object convertBySpi(Object value, Class<?> targetType, Object fallback) {
        JQuickTypeConverter converter = JQuickTypeConverterManager.getConverter(targetType);
        if (converter != null) {
            return converter.convert(value, targetType);
        }
        if (fallback instanceof RuntimeException) {
            throw (RuntimeException) fallback;
        }
        return fallback;
    }

    private static BigDecimal toBigDecimal(Object value) {
        try {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof BigInteger) {
                return new BigDecimal((BigInteger) value);
            }
            return new BigDecimal(value.toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Date toDate(Object value) {
        Date date;
        if (value instanceof Date) {
            date = (Date) value;
        } else if (value instanceof Calendar) {
            date = ((Calendar) value).getTime();
        } else if (value instanceof Instant) {
            date = Date.from((Instant) value);
        } else if (value instanceof OffsetDateTime) {
            date = Date.from(((OffsetDateTime) value).toInstant());
        } else if (value instanceof ZonedDateTime) {
            date = Date.from(((ZonedDateTime) value).toInstant());
        } else if (value instanceof LocalDateTime) {
            date = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof LocalDate) {
            date = Date.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            date = JDateUtil.toDate(value);
        }
        return date;
    }

    private static Date toSqlDate(Date date, Class<?> targetType) {
        if (targetType == java.sql.Timestamp.class) {
            return new java.sql.Timestamp(date.getTime());
        }
        if (targetType == java.sql.Date.class) {
            return new java.sql.Date(date.getTime());
        }
        if (targetType == java.sql.Time.class) {
            return new java.sql.Time(date.getTime());
        }
        return date;
    }

    private static Class<?> box(Class<?> type) {
        if (type == null || !type.isPrimitive()) {
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
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        return type;
    }

    private static Object primitiveDefault(Class<?> type) {
        Class<?> boxed = box(type);
        if (boxed == Boolean.class) {
            return Boolean.FALSE;
        }
        if (boxed == Character.class) {
            return '\0';
        }
        if (boxed == Byte.class) {
            return (byte) 0;
        }
        if (boxed == Short.class) {
            return (short) 0;
        }
        if (boxed == Integer.class) {
            return 0;
        }
        if (boxed == Long.class) {
            return 0L;
        }
        if (boxed == Float.class) {
            return 0F;
        }
        if (boxed == Double.class) {
            return 0D;
        }
        return null;
    }

    /**
     * 查找 setter 方法。
     */
    private static Method findSetter(Class<?> clazz, String propertyName, Class<?> propertyType) {
        String setterName = "set" + Character.toUpperCase(propertyName.charAt(0))
                + propertyName.substring(1);
        try {
            return clazz.getMethod(setterName, propertyType);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(propertyName, propertyType);
            } catch (NoSuchMethodException e2) {
                return null;
            }
        }
    }

    /**
     * 转换器接口。
     */
    public interface RowToBeanConverter<T> {
        T convert(JQuickRow row);
    }

    /**
     * 反射降级转换器（复用 JQuickJavaBeanIntrospection 解析结果）。
     */
    static class ReflectionRowToBeanConverter<T> implements RowToBeanConverter<T> {

        private final Class<T> targetClass;

        private final JQuickJavaBeanIntrospection.PropertyInfo[] props;

        private final Constructor<T> ctor;

        ReflectionRowToBeanConverter(Class<T> clazz) {
            this.targetClass = clazz;
            this.props = JQuickJavaBeanIntrospection.resolve(clazz);
            try {
                this.ctor = clazz.getDeclaredConstructor();
                this.ctor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("target class no constructor : " + clazz.getName(), e);
            }
        }

        @Override
        public T convert(JQuickRow row) {
            return BeanIntrospector.toBean(row, targetClass);
        }
    }

    static class BeanIntrospector {

        private static final Map<Class<?>, Setter[]> SETTER_CACHE = new ConcurrentHashMap<>();

        @SuppressWarnings("unchecked")
        static <T> T toBean(JQuickRow row, Class<T> clazz) {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                Setter[] setters = SETTER_CACHE.computeIfAbsent(clazz, BeanIntrospector::resolveSetters);
                Map<String, Object> data = row.internalMap();
                for (Setter s : setters) {
                    Object value = data.get(s.propertyName());
                    if (value != null) {
                        s.set(instance, convertValue(value, s.propertyType()));
                    }
                }
                return instance;
            } catch (Exception e) {
                return null;
            }
        }

        private static Setter[] resolveSetters(Class<?> clazz) {
            java.util.List<Setter> list = new java.util.ArrayList<>();
            for (Method m : clazz.getMethods()) {
                String name = m.getName();
                if (name.startsWith("set") && name.length() > 3
                        && m.getParameterCount() == 1
                        && Modifier.isPublic(m.getModifiers())) {
                    String prop = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                    list.add(new MethodSetter(prop, m.getParameterTypes()[0], m));
                }
            }
            if (list.isEmpty()) {
                for (Field f : clazz.getDeclaredFields()) {
                    if (!Modifier.isStatic(f.getModifiers())) {
                        f.setAccessible(true);
                        list.add(new FieldSetter(f.getName(), f.getType(), f));
                    }
                }
            }
            return list.toArray(new Setter[0]);
        }

        private static Object convertValue(Object value, Class<?> targetType) {
            return coerce(value, targetType, targetType.isPrimitive());
        }

        /**
         * Setter 抽象接口。
         */
        interface Setter {
            String propertyName();

            Class<?> propertyType();

            void set(Object target, Object value) throws Exception;
        }

        /**
         * Method 类型的 Setter。
         */
        static class MethodSetter implements Setter {
            final String propertyName;
            final Class<?> propertyType;
            final Method method;

            MethodSetter(String propertyName, Class<?> propertyType, Method method) {
                this.propertyName = propertyName;
                this.propertyType = propertyType;
                this.method = method;
            }

            @Override
            public String propertyName() {
                return propertyName;
            }

            @Override
            public Class<?> propertyType() {
                return propertyType;
            }

            @Override
            public void set(Object target, Object value) throws Exception {
                method.invoke(target, value);
            }
        }

        /**
         * Field 类型的 Setter。
         */
        static class FieldSetter implements Setter {
            final String propertyName;
            final Class<?> propertyType;
            final Field field;

            FieldSetter(String propertyName, Class<?> propertyType, Field field) {
                this.propertyName = propertyName;
                this.propertyType = propertyType;
                this.field = field;
            }

            @Override
            public String propertyName() {
                return propertyName;
            }

            @Override
            public Class<?> propertyType() {
                return propertyType;
            }

            @Override
            public void set(Object target, Object value) throws Exception {
                field.set(target, value);
            }
        }
    }
}
