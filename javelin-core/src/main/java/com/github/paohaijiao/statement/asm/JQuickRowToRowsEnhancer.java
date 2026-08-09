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

import com.github.paohaijiao.statement.JQuickRow;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JQuickRow.toRows 方法字节码增强器（备用实现）
 * <p>
 * 核心逻辑已统一在 {@link JQuickRowOptimizer}，本类提供：
 * <ol>
 *   <li>generateEnhancedToRows()：Java agent 场景下替换整个 toRows 静态方法字节码为 Optimizer.toRows 调用</li>
 *   <li>getConverter(Class)：同 JQuickRowOptimizer.createAsmConverter，
 *       使用 {@link JQuickRowConverterGenerator}(COMPUTE_MAXS|COMPUTE_FRAMES) + GeneratedConverter
 *       保证 long/double 占 2 槽位时字节码仍然被 JVM 接受。</li>
 * </ol>
 * <p>
 * 所有反射降级路径复用 Optimizer.BeanIntrospector，<b>永不回调 JQuickRow.toRows()</b>，
 * 避免递归 StackOverflowError。
 */
public class JQuickRowToRowsEnhancer {

    private static final Map<Class<?>, byte[]> CONVERTER_CACHE = new ConcurrentHashMap<>();

    /**
     * 生成增强版 JQuickRow 类字节码（供 Java agent 热替换，非运行时直接使用）。
     * 生成后的 toRows 静态方法直接委托给 {@code JQuickRowOptimizer.toRows}。
     */
    public static byte[] generateEnhancedToRows() {
        return com.jquick.asm.writer.JQuickClassWriterTool
                .builder("com.github.paohaijiao.statement.JQuickRow")
                .access(org.objectweb.asm.Opcodes.ACC_PUBLIC)
                .addMethod(org.objectweb.asm.Opcodes.ACC_PUBLIC | org.objectweb.asm.Opcodes.ACC_STATIC,
                        "toRows",
                        "(Ljava/util/List;)Ljava/util/List;",
                        mv -> {
                            mv.visitCode();
                            mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
                            mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESTATIC,
                                    "com/github/paohaijiao/statement/asm/JQuickRowOptimizer",
                                    "toRows", "(Ljava/util/List;)Ljava/util/List;", false);
                            mv.visitInsn(org.objectweb.asm.Opcodes.ARETURN);
                            mv.visitMaxs(1, 1);
                            mv.visitEnd();
                        })
                .build();
    }

    /**
     * 获取（或生成缓存）某类的专用转换器。
     * ASM 失败则降级到 Optimizer 统一的 BeanIntrospector（零递归 + 与 ASM 同规则字段名）。
     */
    public static RowConverter getConverter(Class<?> elementClass) {
        try {
            //使用新的 generate() API：返回 GeneratedConverter，字节码内部名和 defineClass 名一致
            JQuickRowConverterGenerator.GeneratedConverter gen =
                    CONVERTER_CACHE.containsKey(elementClass)
                            ? new JQuickRowConverterGenerator.GeneratedConverter(
                                    buildDotName(elementClass), CONVERTER_CACHE.get(elementClass))
                            : JQuickRowConverterGenerator.generate(elementClass);

            if (!CONVERTER_CACHE.containsKey(elementClass)) {
                CONVERTER_CACHE.put(elementClass, gen.bytes);
            }

            ByteArrayClassLoader loader = new ByteArrayClassLoader();
            loader.defineClass(gen.converterName, gen.bytes);
            Class<?> converterClass = loader.loadClass(gen.converterName);
            return (RowConverter) converterClass.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            return new ReflectionRowConverter(elementClass);
        }
    }

    private static String buildDotName(Class<?> elementClass) {
        return "com.github.paohaijiao.statement.generated.Converter_"
                + elementClass.getSimpleName() + "_" + System.identityHashCode(elementClass);
    }

    /**
     * 行转换器接口（必须 public，ASM 生成类需要访问）
     */
    public interface RowConverter {
        List<JQuickRow> convert(List<?> list);
    }

    /**
     * 反射降级转换器：复用 Optimizer.BeanIntrospector 统一最优化实现
     */
    static class ReflectionRowConverter implements RowConverter {
        ReflectionRowConverter(Class<?> targetClass) {
            JQuickJavaBeanIntrospection.resolve(targetClass);  // 预热
        }

        @Override
        public List<JQuickRow> convert(List<?> list) {
            return JQuickRowOptimizer.BeanIntrospector.convertList(list);
        }
    }

    /**
     * 字节码类加载器
     */
    static class ByteArrayClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException e) {
                return findClass(name);
            }
        }

        public Class<?> defineClass(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
