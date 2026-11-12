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
import com.github.paohaijiao.i18n.spi.DefaultPackageLookupSPI;
import com.github.paohaijiao.i18n.spi.PackageLookupSPI;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * i18n 全局配置。
 * Global configuration of the i18n module.
 *
 * <p>配置项说明 / Configuration items：</p>
 * <ul>
 *     <li>{@code enablePackageLookup}：是否开启“逐级向上回溯父包查找”。
 *     默认 {@code true}；关闭后只查询 refClass 所在包，随后直接查询全局兜底 bundle。
 *     Whether to enable the package backtracking lookup. Defaults to {@code true}.</li>
 *     <li>{@code fallbackBundleName}：全局兜底 bundle 名称，所有包都找不到 key 时使用。
 *     默认 {@code i18n/messages}，与 {@link I18nUtils} 的全局资源保持一致。
 *     The global fallback bundle name. Defaults to {@code i18n/messages}.</li>
 *     <li>{@code packageLookupSPI} / {@code bundleLoaderSPI}：SPI 扩展点，
 *     未显式设置时通过 {@link ServiceLoader} 自动发现，发现不到则使用内置默认实现。
 *     SPI extension points, resolved by {@link ServiceLoader} when not set explicitly.</li>
 * </ul>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * // 1. 关闭向上回溯，只查当前包 + 全局兜底
 * I18nConfig.setEnablePackageLookup(false);
 *
 * // 2. 更换全局兜底 bundle
 * I18nConfig.setFallbackBundleName("i18n/global-messages");
 *
 * // 3. 注册自定义 SPI
 * I18nConfig.setBundleLoaderSPI(new DatabaseBundleLoaderSPI());
 *
 * // 4. 恢复默认配置（主要供测试隔离使用）
 * I18nConfig.reset();
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 */
public final class I18nConfig {

    /**
     * 默认全局兜底 bundle 名称，与 {@link I18nUtils} 使用的全局资源包保持一致。
     */
    public static final String DEFAULT_FALLBACK_BUNDLE_NAME = "i18n/messages";

    private static volatile boolean enablePackageLookup = true;

    private static volatile String fallbackBundleName = DEFAULT_FALLBACK_BUNDLE_NAME;

    private static volatile PackageLookupSPI packageLookupSPI;

    private static volatile BundleLoaderSPI bundleLoaderSPI;

    private I18nConfig() {
    }

    /**
     * 判断是否开启逐级向上回溯父包查找。
     *
     * <p>Whether the package backtracking lookup is enabled.</p>
     *
     * @return {@code true} 表示开启回溯（默认），{@code false} 表示只查当前包
     */
    public static boolean isEnablePackageLookup() {
        return enablePackageLookup;
    }

    /**
     * 设置是否开启逐级向上回溯父包查找。
     *
     * <p>Set whether to enable the package backtracking lookup.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * I18nConfig.setEnablePackageLookup(false);
     * // 此后 com.github.paohaijiao.util 包内查不到 key 时，不再回溯到 com.github.paohaijiao
     * }</pre>
     *
     * @param enablePackageLookup {@code true} 开启回溯，{@code false} 关闭回溯
     */
    public static void setEnablePackageLookup(boolean enablePackageLookup) {
        I18nConfig.enablePackageLookup = enablePackageLookup;
    }

    /**
     * 获取全局兜底 bundle 名称。
     *
     * <p>Get the global fallback bundle name.</p>
     *
     * @return 兜底 bundle 名称，默认 {@value #DEFAULT_FALLBACK_BUNDLE_NAME}
     */
    public static String getFallbackBundleName() {
        return fallbackBundleName;
    }

    /**
     * 设置全局兜底 bundle 名称。
     *
     * <p>Set the global fallback bundle name.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * I18nConfig.setFallbackBundleName("i18n/global-messages");
     * }</pre>
     *
     * @param fallbackBundleName 兜底 bundle 名称，传入 {@code null} 或空字符串时恢复默认值
     */
    public static void setFallbackBundleName(String fallbackBundleName) {
        if (fallbackBundleName == null || fallbackBundleName.trim().isEmpty()) {
            I18nConfig.fallbackBundleName = DEFAULT_FALLBACK_BUNDLE_NAME;
        } else {
            I18nConfig.fallbackBundleName = fallbackBundleName.trim();
        }
    }

    /**
     * 获取包回溯遍历策略，未设置时自动发现。
     *
     * <p>Get the package lookup SPI, resolved lazily via {@link ServiceLoader}.</p>
     *
     * @return 包回溯遍历策略，永不为 {@code null}
     */
    public static PackageLookupSPI getPackageLookupSPI() {
        PackageLookupSPI spi = packageLookupSPI;
        if (spi == null) {
            spi = discover(PackageLookupSPI.class, DefaultPackageLookupSPI.class);
            packageLookupSPI = spi;
        }
        return spi;
    }

    /**
     * 设置包回溯遍历策略，用于自定义包的遍历顺序。
     *
     * <p>Set a custom package lookup SPI; pass {@code null} to fall back to auto discovery.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * I18nConfig.setPackageLookupSPI(startPackage -> Arrays.asList(
         JQuickPackageUtils.getParentPackage(startPackage), startPackage));
     * }</pre>
     *
     * @param packageLookupSPI 自定义策略，{@code null} 表示恢复自动发现
     */
    public static void setPackageLookupSPI(PackageLookupSPI packageLookupSPI) {
        I18nConfig.packageLookupSPI = packageLookupSPI;
    }

    /**
     * 获取资源包加载策略，未设置时自动发现。
     *
     * <p>Get the bundle loader SPI, resolved lazily via {@link ServiceLoader}.</p>
     *
     * @return 资源包加载策略，永不为 {@code null}
     */
    public static BundleLoaderSPI getBundleLoaderSPI() {
        BundleLoaderSPI spi = bundleLoaderSPI;
        if (spi == null) {
            spi = discover(BundleLoaderSPI.class, DefaultBundleLoaderSPI.class);
            bundleLoaderSPI = spi;
        }
        return spi;
    }

    /**
     * 设置资源包加载策略，用于把文案来源替换为数据库等。
     *
     * <p>Set a custom bundle loader SPI; pass {@code null} to fall back to auto discovery.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * I18nConfig.setBundleLoaderSPI(new DatabaseBundleLoaderSPI());
     * }</pre>
     *
     * @param bundleLoaderSPI 自定义加载策略，{@code null} 表示恢复自动发现
     */
    public static void setBundleLoaderSPI(BundleLoaderSPI bundleLoaderSPI) {
        I18nConfig.bundleLoaderSPI = bundleLoaderSPI;
    }

    /**
     * 恢复全部默认配置，用于测试隔离或运行时重置。
     *
     * <p>Reset all configuration items to their default values.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * I18nConfig.reset();
     * assertEquals(true, I18nConfig.isEnablePackageLookup());
     * }</pre>
     */
    public static void reset() {
        enablePackageLookup = true;
        fallbackBundleName = DEFAULT_FALLBACK_BUNDLE_NAME;
        packageLookupSPI = null;
        bundleLoaderSPI = null;
    }

    /**
     * 先按 SPI 自动发现实现，发现不到则实例化内置默认实现。
     *
     * @param spiType      扩展点类型
     * @param fallbackType 内置默认实现类型
     * @param <T>          扩展点泛型
     * @return 扩展点实例
     */
    private static <T> T discover(Class<T> spiType, Class<? extends T> fallbackType) {
        try {
            Iterator<T> iterator = ServiceLoader.load(spiType).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        } catch (Throwable ignored) {
            // 自定义 SPI 加载失败时不影响默认能力，继续使用内置实现
        }
        try {
            return fallbackType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("无法实例化 i18n 默认扩展实现: " + fallbackType.getName(), e);
        }
    }
}
