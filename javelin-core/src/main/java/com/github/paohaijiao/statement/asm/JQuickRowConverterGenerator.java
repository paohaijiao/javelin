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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * ASM 字节码生成器：为每个 Bean 类生成专用的 List<Bean> → List<JQuickRow> 转换器。
 * <p>
 * 【关键修正】
 * <ol>
 *   <li>直接使用 {@link ClassWriter}(COMPUTE_MAXS|COMPUTE_FRAMES) 自动计算操作数栈/局部变量表最大深度，
 *       避免 long/double 占 2 槽位导致手写栈深度出错 → 字节码被 JVM VerifyError 拒绝。</li>
 *   <li>getter 提取和属性名规则委托给共享的 {@link JQuickJavaBeanIntrospection}，
 *       ASM 字节码和反射降级路径产生完全一致的字段名（含 JavaBean 连续大写保留规则）。</li>
 *   <li>基本类型（byte/short/int/long/float/double/boolean/char）严格区分装箱，引用类型直传。</li>
 * </ol>
 */
public class JQuickRowConverterGenerator {

    /** 生成的转换器内部名前缀（斜杠分隔） */
    private static final String GENERATED_PREFIX = "com/github/paohaijiao/statement/generated/Converter_";
    /** 对应的点分隔前缀（用于 ClassLoader.defineClass 的 name 参数） */
    private static final String GENERATED_PREFIX_DOT = "com.github.paohaijiao.statement.generated.Converter_";

    /**
     * 生成转换器字节码。返回 (converterName, bytecode) 二元组，
     * 让调用者使用同一个 converterName 做 defineClass，避免名称错位。
     */
    public static GeneratedConverter generate(Class<?> targetClass) {
        String idSuffix = targetClass.getSimpleName() + "_" + System.identityHashCode(targetClass);
        String internalName = GENERATED_PREFIX + idSuffix;      // ASM 需要的 / 分隔
        String dotName      = GENERATED_PREFIX_DOT + idSuffix;   // ClassLoader 需要的 . 分隔

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        String rowConverterInternal = Type.getInternalName(JQuickRowOptimizer.RowConverter.class);
        cw.visit(Opcodes.V1_8,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
                internalName,
                null,
                "java/lang/Object",
                new String[]{ rowConverterInternal });
        cw.visitSource("JQuickRowConverter_GEN_" + targetClass.getSimpleName(), null);

        // <init>()V
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);  // COMPUTE_MAXS：传 0 即可，ASM 自动重算
        mv.visitEnd();

        // convert(Ljava/util/List;)Ljava/util/List;
        MethodVisitor cmv = cw.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                "convert",
                "(Ljava/util/List;)Ljava/util/List;",
                // 泛型签名：保留给泛型反射场景（可选）
                "(Ljava/util/List<*>;)Ljava/util/List<Lcom/github/paohaijiao/statement/JQuickRow;>;",
                null);
        writeConvertBody(cmv, targetClass, internalName);
        cmv.visitMaxs(0, 0);  // COMPUTE_MAXS：自动重算
        cmv.visitEnd();

        cw.visitEnd();
        return new GeneratedConverter(dotName, cw.toByteArray());
    }

    /**
     * 兼容老 API：直接返回 byte[]（不推荐，容易和 defineClass 名错位）
     * @deprecated 改用 {@link #generate(Class)} 返回的 converterName + bytes
     */
    @Deprecated
    public static byte[] generateConverter(Class<?> targetClass) {
        return generate(targetClass).bytes;
    }

    public static final class GeneratedConverter {
        public final String converterName;  // 点分隔名（给 ClassLoader.defineClass）
        public final byte[] bytes;
        GeneratedConverter(String name, byte[] b) { this.converterName = name; this.bytes = b; }
    }

    private static void writeConvertBody(MethodVisitor mv,
                                         Class<?> targetClass,
                                         String selfInternal) {
        JQuickJavaBeanIntrospection.PropertyInfo[] props = JQuickJavaBeanIntrospection.resolvePublic(targetClass);
        int propertyCount = props.length;
        String targetInternal = Type.getInternalName(targetClass);
        String jquickRowInternal = "com/github/paohaijiao/statement/JQuickRow";
        String mapInternal = "java/util/Map";

        mv.visitCode();

        // ---- 局部变量索引分配
        // 0: this
        // 1: list (List)
        // 2: result (List)
        // 3: it (Iterator)
        // 4: element (Object)
        // 5: row (JQuickRow)    — Bean 路径
        // 6: internalMap (Map)   — Bean 路径
        final int L_THIS = 0, L_LIST = 1, L_RESULT = 2, L_ITER = 3, L_ELEM = 4, L_ROW = 5, L_MAP = 6;

        // result = new ArrayList<>(list.size())
        mv.visitVarInsn(Opcodes.ALOAD, L_LIST);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I", true);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.SWAP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V", false);
        mv.visitVarInsn(Opcodes.ASTORE, L_RESULT);

        // it = list.iterator()
        mv.visitVarInsn(Opcodes.ALOAD, L_LIST);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
        mv.visitVarInsn(Opcodes.ASTORE, L_ITER);

        Label loopStart = new Label();
        Label loopEnd   = new Label();
        mv.visitLabel(loopStart);

        // it.hasNext()?
        mv.visitVarInsn(Opcodes.ALOAD, L_ITER);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
        mv.visitJumpInsn(Opcodes.IFEQ, loopEnd);

        // element = it.next()
        mv.visitVarInsn(Opcodes.ALOAD, L_ITER);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitVarInsn(Opcodes.ASTORE, L_ELEM);

        // if (element == null) continue
        Label elemOk = new Label();
        mv.visitVarInsn(Opcodes.ALOAD, L_ELEM);
        mv.visitJumpInsn(Opcodes.IFNONNULL, elemOk);
        mv.visitJumpInsn(Opcodes.GOTO, loopStart);
        mv.visitLabel(elemOk);

        // if (element instanceof Map) → 直接包装分支
        Label isMap = new Label();
        Label isBean = new Label();
        mv.visitVarInsn(Opcodes.ALOAD, L_ELEM);
        mv.visitTypeInsn(Opcodes.INSTANCEOF, mapInternal);
        mv.visitJumpInsn(Opcodes.IFNE, isMap);

        mv.visitLabel(isBean);

        // row = new JQuickRow(propertyCount)   ← 预分配容量，避免 HashMap.resize
        mv.visitTypeInsn(Opcodes.NEW, jquickRowInternal);
        mv.visitInsn(Opcodes.DUP);
        pushInt(mv, propertyCount);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, jquickRowInternal, "<init>", "(I)V", false);
        mv.visitVarInsn(Opcodes.ASTORE, L_ROW);

        // internalMap = row.internalMap()  ← public 方法，跨包可调用
        mv.visitVarInsn(Opcodes.ALOAD, L_ROW);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, jquickRowInternal, "internalMap",
                "()L" + mapInternal + ";", false);
        mv.visitVarInsn(Opcodes.ASTORE, L_MAP);

        //依次 put 每个属性值
        // 栈布局循环： ALOAD map → LDC key → ALOAD element → CHECKCAST → INVOKEVIRTUAL getter → (box) → INVOKEINTERFACE Map.put → POP
        for (JQuickJavaBeanIntrospection.PropertyInfo p : props) {
            mv.visitVarInsn(Opcodes.ALOAD, L_MAP);                        // stack: [map]
            mv.visitLdcInsn(p.propertyName);                              // stack: [map, key]
            mv.visitVarInsn(Opcodes.ALOAD, L_ELEM);                       // stack: [map, key, elem]
            mv.visitTypeInsn(Opcodes.CHECKCAST, targetInternal);          // stack: [map, key, casted_elem]
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,                     // stack: [map, key, value]  (value: prim 占 1 或 2 slot)
                    targetInternal, p.methodName, p.methodDesc, false);
            if (p.primitive) {                                            // 基本类型 → 装箱
                boxPrimitive(mv, p.returnType);
            }
            // INVOKEINTERFACE Map.put(k, v) → 返回旧值（丢弃）
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, mapInternal, "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(Opcodes.POP);
        }

        // result.add(row)
        Label addAndContinue = new Label();
        mv.visitVarInsn(Opcodes.ALOAD, L_RESULT);
        mv.visitVarInsn(Opcodes.ALOAD, L_ROW);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add",
                "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(Opcodes.POP);
        mv.visitJumpInsn(Opcodes.GOTO, addAndContinue);

        mv.visitLabel(isMap);
        mv.visitVarInsn(Opcodes.ALOAD, L_ELEM);
        mv.visitTypeInsn(Opcodes.CHECKCAST, mapInternal);
        mv.visitTypeInsn(Opcodes.NEW, jquickRowInternal);
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.SWAP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, jquickRowInternal,
                "<init>", "(Ljava/util/Map;)V", false);

        // result.add(row)
        mv.visitVarInsn(Opcodes.ASTORE, L_ROW);   // 复用 slot
        mv.visitVarInsn(Opcodes.ALOAD, L_RESULT);
        mv.visitVarInsn(Opcodes.ALOAD, L_ROW);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add",
                "(Ljava/lang/Object;)Z", true);
        mv.visitInsn(Opcodes.POP);

        mv.visitLabel(addAndContinue);
        mv.visitJumpInsn(Opcodes.GOTO, loopStart);

        mv.visitLabel(loopEnd);
        mv.visitVarInsn(Opcodes.ALOAD, L_RESULT);
        mv.visitInsn(Opcodes.ARETURN);

        // visitMaxs(0, 0) 由调用方传入 COMPUTE_MAXS → ASM 自动重写
        mv.visitEnd();
    }

    /** 最优 int 常量压栈（ICONST_n / BIPUSH / SIPUSH / LDC） */
    private static void pushInt(MethodVisitor mv, int value) {
        if (value >= -1 && value <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    /** 8 种基本类型 → 对应包装器 valueOf() 装箱（严格匹配类型，无自动提升） */
    static void boxPrimitive(MethodVisitor mv, Class<?> type) {
        if (type == int.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (type == long.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (type == boolean.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (type == double.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (type == float.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (type == short.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (type == byte.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (type == char.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        }
    }
}
