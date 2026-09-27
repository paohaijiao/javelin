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
package com.github.paohaijiao.i18n.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 默认资源包加载策略：从 classpath 读取 UTF-8 编码的 properties 文件。
 * Default bundle loader: read UTF-8 encoded properties files from the classpath.
 *
 * <p>实现要点：</p>
 * <ul>
 *     <li>使用 {@link PropertyResourceBundle} 配合 {@link InputStreamReader}，保证中文等非 ASCII 文案不乱码，
 *     对应 {@code messages_zh_CN.properties} 直接写中文即可。</li>
 *     <li>按 {@link ResourceBundle.Control#getCandidateLocales} 生成的候选顺序（{@code zh_CN -> zh -> ROOT}）
 *     依次探测，第一个存在的文件即生效，等价于 JDK 的资源包语言回退语义。</li>
 *     <li>找不到任何文件时返回 {@code null}，不抛异常。</li>
 * </ul>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * BundleLoaderSPI loader = new DefaultBundleLoaderSPI();
 * ResourceBundle bundle = loader.load("com.github.paohaijiao.util.messages",
 *         Locale.SIMPLIFIED_CHINESE, Thread.currentThread().getContextClassLoader());
 * // 命中 src/main/resources/com/github/paohaijiao/util/messages_zh_CN.properties
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 */
public class DefaultBundleLoaderSPI implements BundleLoaderSPI {

    private static final String PROPERTIES_SUFFIX = ".properties";

    /**
     * {@inheritDoc}
     *
     * <p>从 classpath 按候选 Locale 顺序查找 UTF-8 properties 文件。</p>
     */
    @Override
    public ResourceBundle load(String baseName, Locale locale, ClassLoader classLoader) {
        if (baseName == null || baseName.isEmpty()) {
            return null;
        }
        Locale targetLocale = locale == null ? Locale.ROOT : locale;
        ClassLoader loader = classLoader == null ? defaultClassLoader() : classLoader;
        List<Locale> candidates = ResourceBundle.Control
                .getControl(ResourceBundle.Control.FORMAT_DEFAULT)
                .getCandidateLocales(baseName, targetLocale);
        for (Locale candidate : candidates) {
            String resourceName = toResourceName(baseName, candidate);
            InputStream stream = loader.getResourceAsStream(resourceName);
            if (stream == null) {
                continue;
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return new PropertyResourceBundle(reader);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取默认类加载器：优先线程上下文类加载器，其次当前类加载器，最后系统类加载器。
     *
     * @return 可用的类加载器
     */
    public static ClassLoader defaultClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        ClassLoader ownClassLoader = DefaultBundleLoaderSPI.class.getClassLoader();
        if (ownClassLoader != null) {
            return ownClassLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }

    /**
     * 将资源包基名与 Locale 拼成 classpath 资源路径。
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * toResourceName("com.github.paohaijiao.util.messages", Locale.SIMPLIFIED_CHINESE);
     * // 结果：com/github/paohaijiao/util/messages_zh_CN.properties
     * toResourceName("com.github.paohaijiao.util.messages", Locale.ROOT);
     * // 结果：com/github/paohaijiao/util/messages.properties
     * }</pre>
     *
     * @param baseName 资源包基名
     * @param locale   候选 Locale
     * @return classpath 资源路径
     */
    public static String toResourceName(String baseName, Locale locale) {
        String bundleName = baseName;
        if (locale != null && !Locale.ROOT.equals(locale) && !locale.toString().isEmpty()) {
            bundleName = baseName + "_" + locale;
        }
        return bundleName.replace('.', '/') + PROPERTIES_SUFFIX;
    }
}
