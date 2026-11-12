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

import com.github.paohaijiao.i18n.spi.BundleLoaderSPI;
import com.github.paohaijiao.i18n.spi.DefaultBundleLoaderSPI;

import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源包缓存管理器。
 * Resource bundle cache manager.
 *
 * <p>缓存键为 {@code (bundleBaseName, Locale)}，与需求中的 {@code (包名, Locale)} 一一对应；
 * 缓存值为 {@link SoftReference}，在内存紧张时允许 GC 回收，从而避免强引用 ResourceBundle
 * 引发的类加载器泄漏（典型场景：热部署、Web 容器重复加载应用）。</p>
 *
 * <p>Locale 切换不需要失效缓存：不同 Locale 使用不同缓存键，天然隔离，
 * 只会在首次访问时重新加载对应语种的 bundle。</p>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * ResourceBundle bundle = JQuickBundleCacheManager.getBundle(
 *         "com.github.paohaijiao.util.messages", Locale.SIMPLIFIED_CHINESE);
 * if (bundle != null && bundle.containsKey("pkg.a.only")) {
 *     System.out.println(bundle.getString("pkg.a.only"));
 * }
 * // 清空缓存，通常在切换配置文件或测试隔离时调用
 * JQuickBundleCacheManager.clear();
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 */
public final class JQuickBundleCacheManager {

    /**
     * 资源包软引用缓存，键为 {@code baseName#locale}。
     */
    private static final ConcurrentHashMap<String, SoftReference<ResourceBundle>> BUNDLE_CACHE = new ConcurrentHashMap<>();

    private JQuickBundleCacheManager() {
    }

    /**
     * 获取资源包，命中缓存直接返回，未命中则加载并写入缓存。
     *
     * <p>Get the bundle from cache, loading and caching it on miss.</p>
     *
     * @param baseName 资源包基名，例如 {@code com.github.paohaijiao.util.messages}
     * @param locale   目标语言环境，{@code null} 视为 {@link Locale#ROOT}
     * @return 资源包；不存在或加载失败返回 {@code null}
     */
    public static ResourceBundle getBundle(String baseName, Locale locale) {
        if (baseName == null || baseName.isEmpty()) {
            return null;
        }
        Locale targetLocale = locale == null ? Locale.ROOT : locale;
        String cacheKey = toCacheKey(baseName, targetLocale);
        SoftReference<ResourceBundle> reference = BUNDLE_CACHE.get(cacheKey);
        if (reference != null) {
            ResourceBundle cached = reference.get();
            if (cached != null) {
                return cached;
            }
            BUNDLE_CACHE.remove(cacheKey, reference);
        }
        ResourceBundle loaded = loadBundle(baseName, targetLocale);
        if (loaded == null) {
            return null;
        }
        SoftReference<ResourceBundle> newReference = new SoftReference<>(loaded);
        SoftReference<ResourceBundle> existing = BUNDLE_CACHE.putIfAbsent(cacheKey, newReference);
        if (existing == null) {
            return loaded;
        }
        ResourceBundle existingBundle = existing.get();
        if (existingBundle != null) {
            return existingBundle;
        }
        BUNDLE_CACHE.put(cacheKey, newReference);
        return loaded;
    }

    /**
     * 直接加载资源包，不经过缓存。
     *
     * <p>Load the bundle bypassing the cache, using the configured loader SPI.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * // 用于校验某个语种的 properties 是否存在
     * ResourceBundle bundle = JQuickBundleCacheManager.loadBundle("i18n/messages", Locale.US);
     * }</pre>
     *
     * @param baseName 资源包基名
     * @param locale   目标语言环境，{@code null} 视为 {@link Locale#ROOT}
     * @return 资源包；不存在返回 {@code null}
     */
    public static ResourceBundle loadBundle(String baseName, Locale locale) {
        if (baseName == null || baseName.isEmpty()) {
            return null;
        }
        Locale targetLocale = locale == null ? Locale.ROOT : locale;
        BundleLoaderSPI loader = I18nConfig.getBundleLoaderSPI();
        if (loader == null) {
            loader = new DefaultBundleLoaderSPI();
        }
        return loader.load(baseName, targetLocale, DefaultBundleLoaderSPI.defaultClassLoader());
    }

    /**
     * 清空资源包缓存。
     *
     * <p>Clear all cached bundles.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * JQuickBundleCacheManager.clear();
     * }</pre>
     */
    public static void clear() {
        BUNDLE_CACHE.clear();
    }

    /**
     * 获取当前缓存条目数量，便于监控与测试断言。
     *
     * <p>Return the number of cached entries.</p>
     *
     * @return 缓存条目数量
     */
    public static int size() {
        return BUNDLE_CACHE.size();
    }

    /**
     * 生成缓存键。
     *
     * @param baseName 资源包基名
     * @param locale   目标语言环境
     * @return 缓存键，形如 {@code com.github.paohaijiao.util.messages#zh_CN}
     */
    private static String toCacheKey(String baseName, Locale locale) {
        return baseName + "#" + locale;
    }
}
