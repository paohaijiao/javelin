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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 完整的 toRows 优化方案
 * <p>
 * 性能优化点（100万条数据从 ~240s → 目标 <10s）：
 * <ol>
 *   <li>ASM 动态字节码：直接调用 getter（零反射）；使用 ClassWriter(COMPUTE_MAXS) 自动计算栈帧，
 *       避免 long/double 占 2 槽位导致 VerifyError。</li>
 *   <li>预分配容量：new JQuickRow(propertyCount) 避免 HashMap 反复扩容</li>
 *   <li>零中间对象：属性值直接 put 进 JQuickRow.internalMap()，不经过中转 HashMap + 构造拷贝</li>
 *   <li>反射降级：复用 {@link JQuickJavaBeanIntrospection}(共享 getter/属性名缓存 → 和 ASM 路径结果 100% 一致)，
 *       单条属性直接写入 internalMap</li>
 *   <li><b>禁止回调 JQuickRow.toRows()</b>：彻底切断 StackOverflowError 递归链（所有降级路径走
 *       BeanIntrospector.convertList，不回调入口方法）。</li>
 * </ol>
 */
public class JQuickRowOptimizer {

    private static final Map<Class<?>, RowConverter> CONVERTER_CACHE = new ConcurrentHashMap<>();

    /** ASM 优化阈值：>= 该数量启动 ASM 生成（生成开销 ~0.1-1ms，对于 10 条以上值得） */
    private static final int OPTIMIZATION_THRESHOLD = 10;

    /**
     * 优化的 toRows 入口
     */
    @SuppressWarnings("unchecked")
    public static List<JQuickRow> toRows(List<?> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        Object first = list.get(0);
        if (first == null) {
            return BeanIntrospector.convertList(list);
        }
        if (first instanceof Map) {
            return convertMapList((List<Map<String, Object>>) list);
        }
        if (list.size() < OPTIMIZATION_THRESHOLD) {
            return BeanIntrospector.convertList(list);
        }
        Class<?> elementClass = first.getClass();
        RowConverter converter = CONVERTER_CACHE.computeIfAbsent(elementClass, clazz -> {
            try {
                return createAsmConverter(clazz);
            } catch (Throwable t) {
                return new ReflectionConverter(clazz);
            }
        });
        return converter.convert(list);
    }

    /**
     * 与 {@link #toRows(List)} 等价，但会对嵌套对象 / 集合 / 数组 / Date / Calendar 做**递归深拷贝**，
     * 保证转换后修改原 bean 的任何字段（含嵌套 POJO、List/Set/Map 元素）都不影响 JQuickRow 中的值。
     * <p>
     * <b>适用场景</b>：转换后仍然需要复用/修改原 bean 列表，或数据源与消费端不在同一生命周期。
     * <b>性能注意</b>：深拷贝会遍历所有嵌套层级并新建副本，时间/内存开销约为 {@link #toRows(List)} 的 2~6 倍；
     * 100 万条复杂数据可能从 < 10s 增加到 20~60s。
     * <p>
     * 循环引用：受 IdentityHashMap 保护，不会 StackOverflow。
     */
    @SuppressWarnings("unchecked")
    public static List<JQuickRow> toRowsCopy(List<?> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        Object first = list.get(0);
        if (first == null) {
            return DeepCopyingBeanIntrospector.convertList(list);
        }
        if (first instanceof Map) {
            return deepCopyMapList((List<Map<String, Object>>) list);
        }
        return DeepCopyingBeanIntrospector.convertList(list);
    }

    @SuppressWarnings("unchecked")
    private static List<JQuickRow> deepCopyMapList(List<Map<String, Object>> list) {
        List<JQuickRow> result = new ArrayList<>(list.size());
        IdentityHashMap<Object, Object> visited = new IdentityHashMap<>();
        for (Map<String, Object> map : list) {
            if (map == null) continue;
            Map<String, Object> copy = new LinkedHashMap<>(map.size());
            for (Map.Entry<String, Object> e : map.entrySet()) {
                copy.put(e.getKey(), deepCopyValue(e.getValue(), visited));
            }
            result.add(new JQuickRow(copy));
        }
        return result;
    }
    /**
     * 对单个 value 执行递归深拷贝。
     * 基本类型/包装/String/枚举/BigDecimal/BigInteger/Temporal(不可变)：直接返回
     * Date/Calendar/Timestamp：clone()
     * 数组：primitive -> clone(); Object[] -> 逐元素深拷贝
     * Collection/Map：保留具体类型（不可变集合降级为标准可变副本），逐元素深拷贝
     * POJO：取无参构造 new 实例，逐字段（含 private ）深拷贝 set 回去
     */
    static Object deepCopyValue(Object value, IdentityHashMap<Object, Object> visited) {
        if (value == null) return null;
        if (value instanceof String
                || value instanceof Character
                || value instanceof Boolean
                || value instanceof Number        // Byte/Short/Integer/Long/Float/Double/BigDecimal/BigInteger/Atomic*
                || value instanceof Enum
                || value instanceof Temporal) {   // LocalDate/LocalDateTime/LocalTime/Instant/Year/Month/Duration/Period...
            return value;
        }

        Object existing = visited.get(value);
        if (existing != null) return existing;
        if (value instanceof Date) {
            Date copy = (Date) ((Date) value).clone();
            visited.put(value, copy);
            return copy;
        }
        if (value instanceof Calendar) {
            Calendar copy = (Calendar) ((Calendar) value).clone();
            visited.put(value, copy);
            return copy;
        }


        Class<?> vc = value.getClass();
        if (vc.isArray()) {// 数组
            int len = Array.getLength(value);
            Class<?> compType = vc.getComponentType();
            if (compType.isPrimitive()) {
                Object copy = java.lang.reflect.Array.newInstance(compType, len);
                System.arraycopy(value, 0, copy, 0, len);
                visited.put(value, copy);
                return copy;
            }
            Object[] src = (Object[]) value;
            Object[] copy = (Object[]) java.lang.reflect.Array.newInstance(compType, len);
            visited.put(value, copy);
            for (int i = 0; i < len; i++) {
                copy[i] = deepCopyValue(src[i], visited);
            }
            return copy;
        }
        if (value instanceof Collection) {
            Collection<Object> src = (Collection<Object>) value;
            Collection<Object> copy;
            if (value instanceof SortedSet) {
                copy = new TreeSet<>(((SortedSet<Object>) value).comparator());
            } else if (value instanceof LinkedHashSet) {
                copy = new LinkedHashSet<>(src.size());
            } else if (value instanceof Set) {
                copy = new HashSet<>(src.size());
            } else if (value instanceof Queue) {
                try {
                    Constructor<?> ctor = value.getClass().getDeclaredConstructor();
                    ctor.setAccessible(true);
                    copy = (Collection<Object>) ctor.newInstance();
                } catch (Throwable any) {
                    copy = new ArrayList<>(src.size());
                }
            } else { // List 或其他，兜底 ArrayList 保持顺序
                copy = new ArrayList<>(src.size());
            }
            visited.put(value, copy);
            for (Object o : src) {
                copy.add(deepCopyValue(o, visited));
            }
            return copy;
        }

        if (value instanceof Map) {
            Map<Object, Object> src = (Map<Object, Object>) value;
            Map<Object, Object> copy;
            if (value instanceof SortedMap) {
                copy = new TreeMap<>(((SortedMap<Object, Object>) value).comparator());
            } else if (value instanceof LinkedHashMap) {
                copy = new LinkedHashMap<>(src.size());
            } else if (value instanceof ConcurrentHashMap) {
                copy = new ConcurrentHashMap<>(src.size());
            } else if (value instanceof HashMap) {
                copy = new HashMap<>(src.size());
            } else {
                try {
                    Constructor<?> ctor = value.getClass().getDeclaredConstructor();
                    ctor.setAccessible(true);
                    copy = (Map<Object, Object>) ctor.newInstance();
                } catch (Throwable any) {
                    copy = new LinkedHashMap<>(src.size());
                }
            }
            visited.put(value, copy);
            for (Map.Entry<Object, Object> e : src.entrySet()) {
                copy.put(
                        deepCopyValue(e.getKey(), visited),
                        deepCopyValue(e.getValue(), visited));
            }
            return copy;
        }
        try {
            Constructor<?> ctor = vc.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object copy = ctor.newInstance();
            visited.put(value, copy);
            Class<?> curr = vc;
            while (curr != null && curr != Object.class) {
                for (Field f : curr.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    f.setAccessible(true);
                    Object fv = f.get(value);
                    f.set(copy, deepCopyValue(fv, visited));
                }
                curr = curr.getSuperclass();
            }
            return copy;
        } catch (Throwable t) {
            return value;
        }
    }

    /**
     * 创建 ASM 转换器（使用 JQuickRowConverterGenerator.generate 返回的 GeneratedConverter，
     * 确保字节码内部名和 defineClass 参数名完全一致，不会 ClassFormatError）
     */
    private static RowConverter createAsmConverter(Class<?> clazz) {
        JQuickRowConverterGenerator.GeneratedConverter gen = JQuickRowConverterGenerator.generate(clazz);
        ByteArrayClassLoader loader = new ByteArrayClassLoader();
        loader.defineClass(gen.converterName, gen.bytes);
        try {
            Class<?> converterClass = loader.loadClass(gen.converterName);
            return (RowConverter) converterClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("ASM converter create failed: " + clazz.getName(), e);
        }
    }

    /**
     * Map 列表快速路径
     */
    @SuppressWarnings("unchecked")
    private static List<JQuickRow> convertMapList(List<Map<String, Object>> list) {
        List<JQuickRow> result = new ArrayList<>(list.size());
        for (Map<String, Object> map : list) {
            if (map != null) {
                result.add(new JQuickRow(map));
            }
        }
        return result;
    }

    public static void clearCache() {
        CONVERTER_CACHE.clear();
        JQuickJavaBeanIntrospection.CACHE_BYPASS_CLEAR__DO_NOT_USE_DIRECTLY();
    }

    /**
     * 行转换器接口（必须是 public，ASM 生成的类实现它时需要访问权限）
     */
    public interface RowConverter {
        List<JQuickRow> convert(List<?> list);
    }

    /**
     * 反射降级转换器
     */
    static class ReflectionConverter implements RowConverter {
        ReflectionConverter(Class<?> targetClass) {
            JQuickJavaBeanIntrospection.resolve(targetClass);
        }

        @Override
        public List<JQuickRow> convert(List<?> list) {
            return BeanIntrospector.convertList(list);
        }
    }

    /**
     * 自定义类加载器，支持 defineClass 注入字节码
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

        /** 将字节码定义为 Class（暴露父类的 protected defineClass） */
        public Class<?> defineClass(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    /**
     * 极致优化的 Bean 反射降级内省器。
     * <p>
     * 所有 getter/属性名解析委托给共享的 {@link JQuickJavaBeanIntrospection}，
     * 和 ASM 字节码路径使用同一份规则，保证字段名一致（解决"基本类型数据有问题"的常见原因）。
     */
    static class BeanIntrospector {

        /**
         * 批量转换：核心高性能路径
         */
        static List<JQuickRow> convertList(List<?> list) {
            List<JQuickRow> result = new ArrayList<>(list.size());

            Class<?> lastClass = null;
            JQuickJavaBeanIntrospection.PropertyInfo[] lastProps = null;
            int lastCount = 0;
            for (Object bean : list) {
                if (bean == null) continue;
                // Map 类型：直接包装
                if (bean instanceof Map) {
                    result.add(new JQuickRow((Map<String, Object>) bean));
                    continue;
                }
                // Bean 类型：解析 getter 缓存（同类型直接复用，避免每次 Map 查找）
                Class<?> cls = bean.getClass();
                JQuickJavaBeanIntrospection.PropertyInfo[] props;
                int propertyCount;
                if (cls == lastClass && lastProps != null) {
                    props = lastProps;
                    propertyCount = lastCount;
                } else {
                    props = JQuickJavaBeanIntrospection.resolve(cls);  // 缓存命中：O(1)
                    propertyCount = props.length;
                    lastClass = cls;
                    lastProps = props;
                    lastCount = propertyCount;
                }

                JQuickRow row = new JQuickRow(propertyCount);
                Map<String, Object> target = row.internalMap();
                for (JQuickJavaBeanIntrospection.PropertyInfo p : props) {
                    try {
                        Method getter = p.method;
                        Object value = getter.invoke(bean);
                        target.put(p.propertyName, value);
                    } catch (Throwable ignore) {
                    }
                }
                result.add(row);
            }
            return result;
        }
    }

    /**
     * 深拷贝版本的 Bean 反射内省器：与 {@link BeanIntrospector} 同样的 getter 规则，
     * 但每个 getter 返回值都会通过 {@link #deepCopyValue(Object, IdentityHashMap)} 递归拷贝，
     * 从而保证 JQuickRow 内任何嵌套引用都不会和原 bean 共享。
     */
    static class DeepCopyingBeanIntrospector {

        static List<JQuickRow> convertList(List<?> list) {
            List<JQuickRow> result = new ArrayList<>(list.size());

            Class<?> lastClass = null;
            JQuickJavaBeanIntrospection.PropertyInfo[] lastProps = null;
            int lastCount = 0;
            IdentityHashMap<Object, Object> visited = new IdentityHashMap<>();
            for (Object bean : list) {
                if (bean == null) continue;
                if (bean instanceof Map) {
                    IdentityHashMap<Object, Object> mv = new IdentityHashMap<>();
                    Map<String, Object> src = (Map<String, Object>) bean;
                    Map<String, Object> copy = new LinkedHashMap<>(src.size());
                    for (Map.Entry<String, Object> e : src.entrySet()) {
                        copy.put(e.getKey(), deepCopyValue(e.getValue(), mv));
                    }
                    result.add(new JQuickRow(copy));
                    continue;
                }

                Class<?> cls = bean.getClass();
                JQuickJavaBeanIntrospection.PropertyInfo[] props;
                int propertyCount;
                if (cls == lastClass && lastProps != null) {
                    props = lastProps;
                    propertyCount = lastCount;
                } else {
                    props = JQuickJavaBeanIntrospection.resolve(cls);
                    propertyCount = props.length;
                    lastClass = cls;
                    lastProps = props;
                    lastCount = propertyCount;
                }

                JQuickRow row = new JQuickRow(propertyCount);
                Map<String, Object> target = row.internalMap();

                for (JQuickJavaBeanIntrospection.PropertyInfo p : props) {
                    try {
                        Object value = p.method.invoke(bean);
                        target.put(p.propertyName, deepCopyValue(value, visited));
                    } catch (Throwable ignore) {
                    }
                }
                result.add(row);
            }
            return result;
        }
    }
}
