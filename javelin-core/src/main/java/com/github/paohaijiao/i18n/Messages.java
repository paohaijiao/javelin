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
package com.github.paohaijiao.i18n;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.i18n.spi.PackageLookupSPI;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 包级国际化消息入口，设计模型对齐 Kettle(Pentaho DI) 的 {@code Messages}。
 * Package-scoped i18n message entry, aligned with the Kettle (Pentaho DI) {@code Messages} design.
 *
 * <p>核心约定：</p>
 * <ol>
 *     <li><b>资源文件按包存放</b>：每个 Java 包目录下放 {@code messages_{locale}.properties}，
 *     例如 {@code com/github/paohaijiao/util/messages_zh_CN.properties}，
 *     同一包下所有类共享同一个 bundle。</li>
 *     <li><b>key 由人工编写</b>：{@code key} 是人工维护的字符串，
 *     不会根据类名、字段名自动拼接生成。</li>
 *     <li><b>就近优先 + 逐级回溯</b>：从 {@code refClass} 所在包开始查找，
 *     当前包命中 key 立即返回；未命中则向上回溯父包，逐级直到顶级包；
 *     全部失败后查询全局兜底 bundle。</li>
 *     <li><b>永不抛异常</b>：找不到 key 时返回 key 原文并输出 DEBUG 日志。</li>
 * </ol>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * // 1. 当前包 com.github.paohaijiao.util 下的类，直接取本包文案
 * //    资源文件：src/main/resources/com/github/paohaijiao/util/messages_zh_CN.properties
 * //    welcome=欢迎使用 javelin
 * String text = Messages.getString(JQuickStringUtils.class, "welcome");
 *
 * // 2. 带占位符（JDK MessageFormat 语法）
 * //    greeting=你好，{0}！当前共有 {1} 个任务
 * String greeting = Messages.getString(JQuickStringUtils.class, "greeting", "张三", 3);
 * // 结果：你好，张三！当前共有 3 个任务
 *
 * // 3. 按 Package 调用，适合框架层只持有包信息时使用
 * String fromPackage = Messages.getString(JQuickStringUtils.class.getPackage(), "welcome");
 *
 * // 4. 找不到 key 时返回 key 原文，不抛异常
 * String fallback = Messages.getString(JQuickStringUtils.class, "not.exists.key");
 * // 结果：not.exists.key
 *
 * // 5. 切换语言：与原有 I18nUtils 共享同一套 Locale 设置
 * I18nUtils.setLocale(Locale.SIMPLIFIED_CHINESE);
 * String zh = Messages.getString(JQuickStringUtils.class, "welcome");
 * I18nUtils.setLocale(Locale.US);
 * String en = Messages.getString(JQuickStringUtils.class, "welcome");
 *
 * // 6. 关闭逐级回溯：只查当前包，随后直接走全局兜底
 * I18nConfig.setEnablePackageLookup(false);
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 * @see I18nConfig
 * @see JQuickBundleCacheManager
 * @see JQuickPackageUtils
 */
public final class Messages {

    /**
     * 模块日志输出，DEBUG 日志用于提示 key 未命中等非异常情况。
     */
    public static JConsole console = new JConsole();

    private Messages() {
    }

    /**
     * 获取国际化文案（无参数）。
     *
     * <p>Get the localized message without arguments.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * // 资源文件 com/github/paohaijiao/util/messages_zh_CN.properties: welcome=欢迎使用 javelin
     * String text = Messages.getString(JQuickStringUtils.class, "welcome");
     * }</pre>
     *
     * @param refClass 参考类，仅用于提取所在包；不会用类名参与 key 拼接
     * @param key      人工编写的消息 key
     * @return 命中则返回对应文案；未命中返回 {@code key} 原文；{@code key} 为 {@code null} 时返回 {@code null}
     */
    public static String getString(Class<?> refClass, String key) {
        return resolve(JQuickPackageUtils.getPackageName(refClass), key, null);
    }

    /**
     * 获取国际化文案并替换 JDK MessageFormat 占位符。
     *
     * <p>Get the localized message and format {@code {0}}, {@code {1}} placeholders via
     * {@link MessageFormat}.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * // 资源文件：greeting=你好，{0}！当前共有 {1} 个任务
     * String text = Messages.getString(JQuickStringUtils.class, "greeting", "张三", 3);
     * // 结果：你好，张三！当前共有 3 个任务
     *
     * // 无参数调用不会触发 MessageFormat，文案中的单引号不会被吞掉
     * String raw = Messages.getString(JQuickStringUtils.class, "raw.text");
     * }</pre>
     *
     * @param refClass 参考类，仅用于提取所在包
     * @param key      人工编写的消息 key
     * @param args     MessageFormat 参数，顺序对应 {@code {0}}、{@code {1}}；为空时原样返回文案
     * @return 命中则返回格式化后的文案；未命中返回 {@code key} 原文；{@code key} 为 {@code null} 时返回 {@code null}
     */
    public static String getString(Class<?> refClass, String key, Object... args) {
        return resolve(JQuickPackageUtils.getPackageName(refClass), key, args);
    }

    /**
     * 按 Package 获取国际化文案，适合框架层只持有包信息时使用。
     *
     * <p>Get the localized message by package, useful when only package information is available.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * Package pkg = JQuickStringUtils.class.getPackage();
     * String text = Messages.getString(pkg, "welcome");
     * String formatted = Messages.getString(pkg, "greeting", "李四", 5);
     * }</pre>
     *
     * @param pkg  参考包，{@code null} 时跳过包级查找，仅查询全局兜底 bundle
     * @param key  人工编写的消息 key
     * @param args MessageFormat 参数
     * @return 命中则返回格式化后的文案；未命中返回 {@code key} 原文；{@code key} 为 {@code null} 时返回 {@code null}
     */
    public static String getString(Package pkg, String key, Object... args) {
        return resolve(pkg == null ? "" : pkg.getName(), key, args);
    }

    /**
     * 清空资源包缓存，通常用于语言包热更新或测试隔离。
     *
     * <p>Clear the bundle cache, typically for hot reload or test isolation.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * Messages.clearCache();
     * }</pre>
     */
    public static void clearCache() {
        JQuickBundleCacheManager.clear();
    }

    /**
     * 消息解析主流程：包回溯查找 -> 全局兜底 -> key 原文。
     *
     * @param packageName 起始包名，可能为空字符串
     * @param key         消息 key
     * @param args        MessageFormat 参数
     * @return 解析后的文案
     */
    private static String resolve(String packageName, String key, Object[] args) {
        if (key == null) {
            console.debug("[i18n] key 为 null，直接返回 null。package={0}", packageName);
            return null;
        }
        if (key.trim().isEmpty()) {
            console.debug("[i18n] key 为空白字符串，直接返回原始 key。package={0}", packageName);
            return key;
        }
        Locale locale = currentLocale();
        String pattern = lookupPattern(packageName, key, locale);
        if (pattern == null) {
            console.debug("[i18n] 未找到国际化文案，返回 key 原文。package={0}, key={1}, locale={2}",
                    packageName, key, locale);
            return key;
        }
        return format(pattern, args, key, locale);
    }

    /**
     * 执行“包回溯 + 全局兜底”查找，返回未格式化的文案模板。
     *
     * @param packageName 起始包名
     * @param key         消息 key
     * @param locale      目标语言环境
     * @return 命中返回文案模板，全部未命中返回 {@code null}
     */
    private static String lookupPattern(String packageName, String key, Locale locale) {
        for (String candidatePackage : candidatePackages(packageName)) {
            String baseName = JQuickPackageUtils.toBundleBaseName(candidatePackage);
            ResourceBundle bundle = JQuickBundleCacheManager.getBundle(baseName, locale);
            if (bundle != null && bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        }
        ResourceBundle fallbackBundle = JQuickBundleCacheManager.getBundle(
                I18nConfig.getFallbackBundleName(), locale);
        if (fallbackBundle != null && fallbackBundle.containsKey(key)) {
            return fallbackBundle.getString(key);
        }
        return null;
    }

    /**
     * 计算需要查找的包列表。
     *
     * <p>开启包回溯时使用 {@link PackageLookupSPI} 解析顺序（默认就近优先、逐级向上）；
     * 关闭包回溯时只返回起始包，不再向上查找父包。</p>
     *
     * @param packageName 起始包名
     * @return 按查找优先级排列的包名列表
     */
    private static List<String> candidatePackages(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return Collections.emptyList();
        }
        if (!I18nConfig.isEnablePackageLookup()) {
            return Collections.singletonList(packageName);
        }
        PackageLookupSPI lookupSPI = I18nConfig.getPackageLookupSPI();
        List<String> packages = lookupSPI == null ? null : lookupSPI.resolvePackages(packageName);
        if (packages == null || packages.isEmpty()) {
            return Collections.singletonList(packageName);
        }
        return packages;
    }

    /**
     * 使用 JDK MessageFormat 替换占位符。
     *
     * <p>仅当存在参数时才走 MessageFormat，避免无占位符文案中的单引号被转义处理。</p>
     *
     * @param pattern 文案模板
     * @param args    参数
     * @param key     消息 key，用于日志
     * @param locale  当前语言环境，用于日志
     * @return 格式化后的文案，格式化失败时返回原模板
     */
    private static String format(String pattern, Object[] args, String key, Locale locale) {
        if (args == null || args.length == 0) {
            return pattern;
        }
        try {
            return MessageFormat.format(pattern, args);
        } catch (IllegalArgumentException e) {
            console.debug("[i18n] MessageFormat 格式化失败，返回原始文案。key={0}, locale={1}, error={2}",
                    key, locale, e.getMessage());
            return pattern;
        }
    }

    /**
     * 获取当前语言环境，与原有 {@link I18nUtils} 共享同一套 Locale 设置，保证向后兼容。
     *
     * @return 当前语言环境，永不为 {@code null}
     */
    private static Locale currentLocale() {
        Locale locale = I18nUtils.getCurrentLocale();
        return locale == null ? Locale.getDefault() : locale;
    }
}
