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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公共的 JavaBean getter 内省器（ASM 字节码生成 + 反射降级 共享同一份解析结果，保证字段一致）。
 * <p>
 * 严格遵守 JavaBean 规范：
 * <ol>
 *   <li>{@code getXxx()} → 属性名 xxx（首字母小写，若前两个字母均大写则保持原样：getURL → "URL"）</li>
 *   <li>{@code isXxx()} → 仅允许返回值是 {@code boolean} / {@code Boolean}，属性名提取规则同上</li>
 *   <li>排除 {@code getClass()}、{@code getMetaClass()} 等 Object/Groovy 遗留方法</li>
 *   <li>向上遍历继承链（直到 {@code Object} 但不包含）</li>
 *   <li>非 public 方法：尝试 setAccessible（仅反射降级使用；ASM 生成类只能访问 public getter）</li>
 * </ol>
 */
public final class JQuickJavaBeanIntrospection {

    private JQuickJavaBeanIntrospection() {}

    /** 类 → getter 元信息 缓存 */
    private static final Map<Class<?>, PropertyInfo[]> CACHE = new ConcurrentHashMap<>();

    /**
     * 单个 getter/属性的描述符（并行数组方案：反射和 ASM 都读同一份，保证一致）
     */
    public static final class PropertyInfo {
        /** getter 方法（反射直接 invoke，ASM 直接取 methodName/methodDesc） */
        public final Method method;
        /** getter 方法名（ASM 字节码 INVOKEVIRTUAL 用） */
        public final String methodName;
        /** getter 描述符，如 (I)V（ASM 用） */
        public final String methodDesc;
        /** JavaBean 属性名（JQuickRow Map 的 key） */
        public final String propertyName;
        /** 返回类型（引用类型直接赋值，基本类型调用 boxPrimitive） */
        public final Class<?> returnType;
        /** 是否基本类型（byte/short/int/long/float/double/boolean/char → 需要装箱） */
        public final boolean primitive;
        /** 为 true 表示该 getter 在任意类加载器上下文都是 public → ASM 可访问 */
        public final boolean publiclyAccessible;

        PropertyInfo(Method m) {
            this.method = m;
            this.methodName = m.getName();
            this.methodDesc = org.objectweb.asm.Type.getMethodDescriptor(m);
            this.returnType = m.getReturnType();
            this.primitive = returnType.isPrimitive();
            this.publiclyAccessible = Modifier.isPublic(m.getModifiers())
                    && Modifier.isPublic(m.getDeclaringClass().getModifiers());
            this.propertyName = extractPropertyName(m);
        }
    }

    /**
     * 获取指定类的所有 JavaBean getter 描述符（线程安全、解析一次终身复用）
     */
    public static PropertyInfo[] resolve(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, JQuickJavaBeanIntrospection::doResolve);
    }

    /**
     * 获取 public 的 getter（过滤掉非 public 的，供 ASM 生成器使用）
     */
    public static PropertyInfo[] resolvePublic(Class<?> clazz) {
        PropertyInfo[] all = resolve(clazz);
        int publicCount = 0;
        for (PropertyInfo pi : all) {
            if (pi.publiclyAccessible) publicCount++;
        }
        PropertyInfo[] result = new PropertyInfo[publicCount];
        int w = 0;
        for (PropertyInfo pi : all) {
            if (pi.publiclyAccessible) result[w++] = pi;
        }
        return result;
    }

    /** getter 属性数量（方便 JQuickRow(int) 预分配容量） */
    public static int propertyCount(Class<?> clazz) {
        return resolve(clazz).length;
    }

    /**
     * 仅供 JQuickRowOptimizer.clearCache() 清理缓存（单元测试 / 热部署场景）。
     * 业务代码请勿直接调用。
     */
    public static void CACHE_BYPASS_CLEAR__DO_NOT_USE_DIRECTLY() {
        CACHE.clear();
    }


    private static PropertyInfo[] doResolve(Class<?> clazz) {
        List<PropertyInfo> out = new ArrayList<>(16);
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            Method[] declared;
            try {
                declared = c.getDeclaredMethods();
            } catch (Throwable t) {
                // SecurityManager 拒绝：降级到 c.getMethods()（只能 public）
                declared = c.getMethods();
            }
            for (Method m : declared) {
                // 静态/有参/void 返回 → 不是 getter
                int mod = m.getModifiers();
                if (Modifier.isStatic(mod)) continue;
                if (m.getParameterCount() != 0) continue;
                if (m.getReturnType() == void.class) continue;
                String name = m.getName();

                boolean isGetter = false;
                if (name.startsWith("get") && name.length() > 3) {
                    isGetter = true;
                } else if (name.startsWith("is") && name.length() > 2) {
                    Class<?> rt = m.getReturnType();
                    if (rt == boolean.class || rt == Boolean.class) {
                        isGetter = true;
                    }
                }
                if (!isGetter) continue;
                // 排除 Object.getClass()
                if ("getClass".equals(name) && m.getDeclaringClass() == Object.class) continue;
                if ("getClass".equals(name) && m.getReturnType() == Class.class) continue;
                // 非 public：尝试 setAccessible 让反射降级可以调用（ASM 仍然会跳过）
                if (!Modifier.isPublic(mod) || !Modifier.isPublic(c.getModifiers())) {
                    try {
                        m.setAccessible(true);
                    } catch (Throwable ignore) {
                        // 安全管理器不允许：反射降级时也会跳过（ASM 已经不选它了）
                    }
                }
                out.add(new PropertyInfo(m));
            }
        }
        return out.toArray(new PropertyInfo[0]);
    }

    /**
     * 从 getter 方法名提取 JavaBean 属性名：
     * <pre>
     * getName     → name
     * isEnabled   → enabled
     * getURL      → URL   (前两个字符均大写，保持原样：JavaBean 规范)
     * getUserName → userName
     * isVIP       → VIP   (前两个字符均大写，保持原样)
     * </pre>
     */
    static String extractPropertyName(Method m) {
        String raw;
        String mn = m.getName();
        if (mn.startsWith("get")) raw = mn.substring(3);
        else if (mn.startsWith("is")) raw = mn.substring(2);
        else return mn;

        if (raw.isEmpty()) return mn;
        // JavaBean 规范：如果前两个字符都是大写（如 "URL"、"ID"），则不做转换
        if (raw.length() >= 2
                && Character.isUpperCase(raw.charAt(0))
                && Character.isUpperCase(raw.charAt(1))) {
            return raw;
        }
        // 正常情况：首字母小写
        return Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
    }
}
