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
package com.github.paohaijiao.value;

import ognl.*;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OGNL 表达式工具类
 * 支持 OGNL 3.4+ 版本
 *
 * @author OGNL Utils
 */
public class ValueResolver {

    private static final Map<String, Object> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    private static final MemberAccess DEFAULT_MEMBER_ACCESS = new DefaultMemberAccess();

    private static final ClassResolver DEFAULT_CLASS_RESOLVER = new DefaultClassResolver();

    private static final TypeConverter DEFAULT_TYPE_CONVERTER = new DefaultTypeConverter();

    /**
     * 私有构造函数，防止实例化
     */
    private ValueResolver() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 创建默认的 OGNL 上下文
     */
    public static OgnlContext createContext() {
        return new OgnlContext(
                DEFAULT_CLASS_RESOLVER,
                DEFAULT_TYPE_CONVERTER,
                DEFAULT_MEMBER_ACCESS
        );
    }

    /**
     * 创建带有根对象的 OGNL 上下文
     *
     * @param root 根对象
     */
    public static OgnlContext createContext(Object root) {
        OgnlContext context = createContext();
        context.setRoot(root);
        return context;
    }

    /**
     * 创建带有根对象和上下文变量的 OGNL 上下文
     *
     * @param root       根对象
     * @param contextMap 上下文变量
     */
    public static OgnlContext createContext(Object root, Map<String, Object> contextMap) {
        OgnlContext context = createContext(root);
        if (contextMap != null) {
            for (Map.Entry<String, Object> entry : contextMap.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
        }
        return context;
    }

    /**
     * 执行 OGNL 表达式（无根对象）
     *
     * @param expression OGNL 表达式
     * @param contextMap 上下文变量
     * @return 表达式执行结果
     */
    public static Object evaluate(String expression, Map<String, Object> contextMap) throws OgnlException {
        return evaluate(expression, contextMap, contextMap);
    }

    /**
     * 执行 OGNL 表达式
     *
     * @param expression OGNL 表达式
     * @param root       根对象
     * @param contextMap 上下文变量
     * @return 表达式执行结果
     */
    public static Object evaluate(String expression, Object root, Map<String, Object> contextMap) throws OgnlException {
        OgnlContext context = createContext(root, contextMap);
        return Ognl.getValue(expression, context, context.getRoot());
    }

    /**
     * 执行 OGNL 表达式（带缓存）
     *
     * @param expression OGNL 表达式
     * @param root       根对象
     * @param contextMap 上下文变量
     * @return 表达式执行结果
     */
    public static Object evaluateWithCache(String expression, Object root, Map<String, Object> contextMap) throws OgnlException {
        Object parsedExpr = EXPRESSION_CACHE.get(expression);
        if (parsedExpr == null) {
            parsedExpr = Ognl.parseExpression(expression);
            EXPRESSION_CACHE.put(expression, parsedExpr);
        }

        OgnlContext context = createContext(root, contextMap);
        return Ognl.getValue(parsedExpr, context, context.getRoot());
    }

    /**
     * 设置 OGNL 表达式的值
     *
     * @param expression OGNL 表达式
     * @param root       根对象
     * @param contextMap 上下文变量
     * @param value      要设置的值
     */
    public static void setValue(String expression, Object root, Map<String, Object> contextMap, Object value) throws OgnlException {
        OgnlContext context = createContext(root, contextMap);
        Ognl.setValue(expression, context, context.getRoot(), value);
    }

    /**
     * 设置 OGNL 表达式的值（带缓存）
     *
     * @param expression OGNL 表达式
     * @param root       根对象
     * @param contextMap 上下文变量
     * @param value      要设置的值
     */
    public static void setValueWithCache(String expression, Object root, Map<String, Object> contextMap, Object value) throws OgnlException {
        Object parsedExpr = EXPRESSION_CACHE.get(expression);
        if (parsedExpr == null) {
            parsedExpr = Ognl.parseExpression(expression);
            EXPRESSION_CACHE.put(expression, parsedExpr);
        }

        OgnlContext context = createContext(root, contextMap);
        Ognl.setValue(parsedExpr, context, context.getRoot(), value);
    }

    /**
     * 将模板中的占位符替换为实际值
     * 支持 ${keyName} 和 #{keyName} 两种格式
     *
     * @param template 模板字符串
     * @param params   参数映射
     * @return 替换后的字符串
     */
    public static String renderTemplate(String template, Map<String, Object> params) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        String result = template;
        Pattern dollarPattern = Pattern.compile("\\$\\{([^}]+)\\}");//${keyName}
        Matcher dollarMatcher = dollarPattern.matcher(result);
        StringBuffer sb = new StringBuffer();
        while (dollarMatcher.find()) {
            String key = dollarMatcher.group(1);
            Object value = params.get(key);
            String replacement = value != null ? String.valueOf(value) : "null";
            dollarMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        dollarMatcher.appendTail(sb);
        result = sb.toString();
        Pattern hashPattern = Pattern.compile("#\\{([^}]+)\\}");// #{keyName} support  OGNL  expr
        Matcher hashMatcher = hashPattern.matcher(result);
        sb = new StringBuffer();
        OgnlContext context = createContext(null, params);
        while (hashMatcher.find()) {
            String expression = hashMatcher.group(1);
            String replacement;
            try {
                Object value = Ognl.getValue(expression, context, context.getRoot());
                replacement = value != null ? String.valueOf(value) : "null";
            } catch (OgnlException e) {
                replacement = hashMatcher.group(0); // 保持原样
            }
            hashMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        hashMatcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 渲染模板（带默认值）
     *
     * @param template     模板字符串
     * @param params       参数映射
     * @param defaultValue 默认值（当值为 null 时使用）
     * @return 替换后的字符串
     */
    public static String renderTemplate(String template, Map<String, Object> params, String defaultValue) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        String result = template;
        Pattern dollarPattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher dollarMatcher = dollarPattern.matcher(result);
        StringBuffer sb = new StringBuffer();
        while (dollarMatcher.find()) {
            String key = dollarMatcher.group(1);
            Object value = params.get(key);
            String replacement = value != null ? String.valueOf(value) : defaultValue;
            dollarMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        dollarMatcher.appendTail(sb);
        result = sb.toString();
        Pattern hashPattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher hashMatcher = hashPattern.matcher(result);
        sb = new StringBuffer();
        OgnlContext context = createContext(null, params);
        while (hashMatcher.find()) {
            String expression = hashMatcher.group(1);
            String replacement;
            try {
                Object value = Ognl.getValue(expression, context, context.getRoot());
                replacement = value != null ? String.valueOf(value) : defaultValue;
            } catch (OgnlException e) {
                replacement = hashMatcher.group(0);
            }
            hashMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        hashMatcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 将对象转换为指定类型
     *
     * @param value  原始值
     * @param toType 目标类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertValue(Object value, Class<T> toType) {
        if (value == null) {
            return null;
        }
        if (toType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return (T) DEFAULT_TYPE_CONVERTER.convertValue(null, null, null, null, value, toType);
    }

    /**
     * 判断表达式是否为简单属性访问
     *
     * @param expression 表达式
     * @return 是否为简单属性
     */
    public static boolean isSimpleProperty(String expression) {
        return expression != null && expression.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    /**
     * 清空表达式缓存
     */
    public static void clearCache() {
        EXPRESSION_CACHE.clear();
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return EXPRESSION_CACHE.size();
    }

    /**
     * 默认的 MemberAccess 实现
     * 允许访问所有成员（包括私有成员）
     */
    private static class DefaultMemberAccess implements MemberAccess {

        private final boolean allowPrivate;
        private final boolean allowProtected;
        private final boolean allowPackage;

        public DefaultMemberAccess() {
            this(true, true, true);
        }

        public DefaultMemberAccess(boolean allowPrivate, boolean allowProtected, boolean allowPackage) {
            this.allowPrivate = allowPrivate;
            this.allowProtected = allowProtected;
            this.allowPackage = allowPackage;
        }

        @Override
        public Object setup(OgnlContext context, Object target, Member member, String propertyName) {
            Object result = null;
            if (isAccessible(context, target, member, propertyName)) {
                AccessibleObject accessible = (AccessibleObject) member;
                if (!accessible.isAccessible()) {
                    result = Boolean.TRUE;
                    accessible.setAccessible(true);
                }
            }
            return result;
        }

        @Override
        public void restore(OgnlContext context, Object target, Member member, String propertyName, Object state) {
            if (state != null && (Boolean) state) {
                ((AccessibleObject) member).setAccessible(false);
            }
        }

        @Override
        public boolean isAccessible(OgnlContext context, Object target, Member member, String propertyName) {
            int modifiers = member.getModifiers();
            if (Modifier.isPublic(modifiers)) {
                return true;
            } else if (Modifier.isPrivate(modifiers)) {
                return allowPrivate;
            } else if (Modifier.isProtected(modifiers)) {
                return allowProtected;
            } else {
                return allowPackage;
            }
        }
    }
}