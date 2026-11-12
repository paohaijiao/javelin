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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 资源包加载策略扩展点 / Resource bundle loading strategy SPI。
 *
 * <p>默认实现 {@link DefaultBundleLoaderSPI} 从 classpath 读取 UTF-8 编码的 properties 文件。
 * 自定义实现可以把文案来源替换为数据库、配置中心或远程服务。</p>
 *
 * <p>约定：找不到资源时必须返回 {@code null}，不要抛出 {@link java.util.MissingResourceException}，
 * 由 {@code Messages} 统一决定是否继续向上回溯或走全局兜底。</p>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * // 从数据库读取文案
 * public class DatabaseBundleLoaderSPI implements BundleLoaderSPI {
 *     @Override
 *     public ResourceBundle load(String baseName, Locale locale, ClassLoader classLoader) {
 *         Map<String, String> rows = messageDao.select(baseName, locale.toString());
 *         return rows.isEmpty() ? null : new MapResourceBundle(rows);
 *     }
 * }
 *
 * I18nConfig.setBundleLoaderSPI(new DatabaseBundleLoaderSPI());
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 */
public interface BundleLoaderSPI {

    /**
     * 加载指定基名与 Locale 的资源包。
     *
     * <p>Load the resource bundle for the given base name and locale.</p>
     *
     * @param baseName    资源包基名，例如 {@code com.github.paohaijiao.util.messages}
     * @param locale      目标语言环境，不会为 {@code null}
     * @param classLoader 用于查找资源的类加载器，可能为 {@code null}，实现方需自行兜底
     * @return 加载成功返回资源包；不存在返回 {@code null}
     */
    ResourceBundle load(String baseName, Locale locale, ClassLoader classLoader);
}
