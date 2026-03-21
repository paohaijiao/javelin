package com.github.paohaijiao.string;/*
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

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PlaceholderResolver {

    private static final String DEFAULT_PLACEHOLDER = "{}";

    private static final Pattern NAMED_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?\\}");

    private static final Map<String, Pattern> PATTERN_CACHE = new HashMap<>();

    private final String template;

    private final String placeholderFormat;

    private final Pattern pattern;

    private final List<Object> positionalArgs;

    private final Map<String, Object> namedArgs;

    private final Map<String, Function<Object, String>> converters;

    private boolean strictMode = false;

    private NullStrategy nullStrategy = NullStrategy.EMPTY_STRING;

    private boolean useNamedMode = false;

    private PlaceholderResolver(String template) {
        this(template, DEFAULT_PLACEHOLDER);
    }
    private PlaceholderResolver(String template, String placeholderFormat) {
        this.template = template;
        this.placeholderFormat = placeholderFormat;
        this.pattern = getPattern(placeholderFormat);
        this.positionalArgs = new ArrayList<>();
        this.namedArgs = new HashMap<>();
        this.converters = new HashMap<>();
    }

    /**
     * 创建解析器（使用默认占位符 {}）
     */
    public static PlaceholderResolver use(String template) {
        return new PlaceholderResolver(template);
    }


    /**
     * 创建解析器（使用自定义占位符）
     *
     * @param placeholder 占位符格式，如 "{}", "#{}", "${}" 等
     */
    public static PlaceholderResolver use(String template, String placeholder) {
        return new PlaceholderResolver(template, placeholder);
    }

    /**
     * 创建命名参数解析器（使用 ${name} 格式）
     */
    public static PlaceholderResolver useNamed(String template) {
        PlaceholderResolver resolver = new PlaceholderResolver(template, "${}");
        resolver.useNamedMode = true;
        return resolver;
    }

    /**
     * 获取或创建Pattern（带缓存）
     */
    private static Pattern getPattern(String placeholder) {
        return PATTERN_CACHE.computeIfAbsent(placeholder, key -> {
            String escaped = Pattern.quote(key);
            return Pattern.compile(escaped);
        });
    }

    /**
     * 快速替换（默认占位符 {}）
     */
    public static String resolve(String template, Object... args) {
        return use(template).add(args).resolve();
    }

    /**
     * 快速替换（自定义占位符）
     */
    public static String resolve(String template, String placeholder, Object... args) {
        return use(template, placeholder).add(args).resolve();
    }
    /**
     * 快速替换（列表参数）
     */
    public static String resolve(String template, List<?> args) {
        return use(template).add(args).resolve();
    }

    /**
     * 快速替换（自定义占位符 + 列表参数）
     */
    public static String resolve(String template, String placeholder, List<?> args) {
        return use(template, placeholder).add(args).resolve();
    }

    /**
     * 命名参数快速替换
     */
    public static String resolveNamed(String template, Map<String, ?> args) {
        return useNamed(template).setAll(args).resolve();
    }

    /**
     * 命名参数快速替换（带默认值）
     */
    public static String resolveNamed(String template, String name, Object value, Object... others) {
        PlaceholderResolver resolver = useNamed(template).set(name, value);
        for (int i = 0; i < others.length; i += 2) {
            if (i + 1 < others.length) {
                resolver.set(String.valueOf(others[i]), others[i + 1]);
            }
        }
        return resolver.resolve();
    }

    /**
     * 批量替换
     */
    public static List<String> batchResolve(List<String> templates, Object... args) {
        List<String> results = new ArrayList<>();
        for (String template : templates) {
            results.add(resolve(template, args));
        }
        return results;
    }
    /**
     * 启用严格模式（参数不足时抛出异常）
     */
    public PlaceholderResolver strict() {
        this.strictMode = true;
        return this;
    }

    /**
     * 设置空值处理策略
     */
    public PlaceholderResolver nullStrategy(NullStrategy strategy) {
        this.nullStrategy = strategy;
        return this;
    }

    /**
     * 添加顺序参数（可变参数）
     */
    public PlaceholderResolver add(Object... args) {
        if (args != null) {
            Collections.addAll(this.positionalArgs, args);
        }
        return this;
    }

    /**
     * 添加顺序参数（列表）
     */
    public PlaceholderResolver add(List<?> args) {
        if (args != null) {
            this.positionalArgs.addAll(args);
        }
        return this;
    }
    /**
     * 添加命名参数
     */
    public PlaceholderResolver set(String name, Object value) {
        this.namedArgs.put(name, value);
        return this;
    }

    /**
     * 添加命名参数（带转换器）
     */
    public PlaceholderResolver set(String name, Object value, Function<Object, String> converter) {
        this.namedArgs.put(name, value);
        this.converters.put(name, converter);
        return this;
    }

    /**
     * 批量添加命名参数
     */
    public PlaceholderResolver setAll(Map<String, ?> args) {
        if (args != null) {
            this.namedArgs.putAll(args);
        }
        return this;
    }

    /**
     * 执行替换
     */
    public String resolve() {
        if (template == null) {
            return null;
        }

        if (useNamedMode) {
            return resolveNamedPlaceholders();
        } else {
            return resolvePositionalPlaceholders();
        }
    }

    /**
     * 替换顺序占位符
     */
    private String resolvePositionalPlaceholders() {
        if (positionalArgs.isEmpty()) {
            return template;
        }
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();
        int index = 0;
        int placeholderCount = 0;
        while (matcher.find()) {
            placeholderCount++;
            Object value = getPositionalValue(index);
            String replacement = formatValue(value);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            index++;
        }
        matcher.appendTail(result);
        if (strictMode && index > positionalArgs.size()) {
            throw new IllegalArgumentException(String.format("参数不足: 需要 %d 个参数，实际提供了 %d 个", index, positionalArgs.size()));
        }
        return result.toString();
    }

    /**
     * 替换命名占位符（支持 ${name} 和 ${name:default}）
     */
    private String resolveNamedPlaceholders() {
        Matcher matcher = NAMED_PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        Set<String> unresolvedPlaceholders = new HashSet<>();
        while (matcher.find()) {
            result.append(template, lastEnd, matcher.start());
            String name = matcher.group(1);
            String defaultValue = matcher.group(2);
            String replacement = getNamedValue(name, defaultValue);
            if (replacement != null && replacement.contains("${")) {
                unresolvedPlaceholders.add(name);
            }
            result.append(replacement);
            lastEnd = matcher.end();
        }
        result.append(template, lastEnd, template.length());
        String resolved = result.toString();
        if (strictMode && !unresolvedPlaceholders.isEmpty()) {
            throw new IllegalArgumentException("未解析的占位符: " + unresolvedPlaceholders);
        }
        if (resolved.contains("${") && !resolved.equals(template)) {
            return resolveNamedPlaceholders(resolved);
        }

        return resolved;
    }

    /**
     * 递归解析嵌套占位符
     */
    private String resolveNamedPlaceholders(String text) {
        Matcher matcher = NAMED_PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            String name = matcher.group(1);
            String defaultValue = matcher.group(2);
            String replacement = getNamedValue(name, defaultValue);
            result.append(replacement);
            lastEnd = matcher.end();
        }
        result.append(text, lastEnd, text.length());
        String resolved = result.toString();
        if (resolved.contains("${") && !resolved.equals(text)) {
            return resolveNamedPlaceholders(resolved);
        }
        return resolved;
    }

    /**
     * 获取顺序参数值
     */
    private Object getPositionalValue(int index) {
        if (index < positionalArgs.size()) {
            return positionalArgs.get(index);
        }
        if (strictMode) {
            throw new IllegalArgumentException("缺少第 " + (index + 1) + " 个参数");
        }
        return null;
    }

    /**
     * 获取命名参数值
     */
    private String getNamedValue(String name, String defaultValue) {
        Object value = namedArgs.get(name);
        if (value != null) {
            if (converters.containsKey(name)) {
                return converters.get(name).apply(value);
            }
            return String.valueOf(value);
        }
        if (defaultValue != null) {
            return defaultValue;
        }
        return handleNullValue(null);
    }

    /**
     * 格式化值
     */
    private String formatValue(Object value) {
        if (value == null) {
            return handleNullValue(null);
        }
        return String.valueOf(value);
    }

    /**
     * 处理空值
     */
    private String handleNullValue(String defaultValue) {
        switch (nullStrategy) {
            case NULL_STRING:
                return "null";
            case THROW_EXCEPTION:
                throw new IllegalArgumentException("遇到空值");
            case KEEP_PLACEHOLDER:
                return placeholderFormat;
            case EMPTY_STRING:
            default:
                return defaultValue != null ? defaultValue : "";
        }
    }
    /**
     * 带格式化数字的参数
     */
    public PlaceholderResolver setNumber(String name, Number value, String format) {
        return set(name, value, v -> String.format(format, v));
    }

    /**
     * 带格式化日期的参数
     */
    public PlaceholderResolver setDate(String name, Date value, String format) {
        return set(name, value, v -> {
            if (v instanceof Date) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
                return sdf.format((Date) v);
            }
            return String.valueOf(v);
        });
    }

    /**
     * 条件添加参数
     */
    public PlaceholderResolver condition(boolean condition, String name, Object trueValue, Object falseValue) {
        if (condition) {
            set(name, trueValue);
        } else {
            set(name, falseValue);
        }
        return this;
    }

    /**
     * 链式条件操作
     */
    public PlaceholderResolver when(boolean condition, java.util.function.Consumer<PlaceholderResolver> action) {
        if (condition) {
            action.accept(this);
        }
        return this;
    }

    /**
     * 清空所有参数
     */
    public PlaceholderResolver clear() {
        this.positionalArgs.clear();
        this.namedArgs.clear();
        this.converters.clear();
        return this;
    }

    /**
     * 复制解析器
     */
    public PlaceholderResolver copy() {
        PlaceholderResolver copy = new PlaceholderResolver(template, placeholderFormat);
        copy.positionalArgs.addAll(this.positionalArgs);
        copy.namedArgs.putAll(this.namedArgs);
        copy.converters.putAll(this.converters);
        copy.strictMode = this.strictMode;
        copy.nullStrategy = this.nullStrategy;
        copy.useNamedMode = this.useNamedMode;
        return copy;
    }

    /**
     * 空值处理策略
     */
    public enum NullStrategy {
        EMPTY_STRING,
        NULL_STRING,
        THROW_EXCEPTION,
        KEEP_PLACEHOLDER
    }
}