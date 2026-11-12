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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 包名工具类，提供包名提取与向上回溯展开能力。
 * Package name utility: extract package name and expand package ancestors.
 *
 * <p>该工具类是 Kettle 风格包回溯查找的基础设施，只做纯字符串与 Class 元数据处理，不涉及资源加载。</p>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * JQuickPackageUtils.getPackageName(String.class);                       // "java.lang"
 * JQuickPackageUtils.getPackageName(JQuickSampleUtil.Inner.class);       // 内部类取外部类包名
 * JQuickPackageUtils.expandToAncestors("com.github.paohaijiao.util");    // [com.github.paohaijiao.util, com.github.paohaijiao, com.github, com]
 * JQuickPackageUtils.toBundleBaseName("com.github.paohaijiao.util");     // "com.github.paohaijiao.util.messages"
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 */
public final class JQuickPackageUtils {

    /**
     * 包内共享资源包的文件名前缀，即 {@code messages_zh_CN.properties} 中的 {@code messages}。
     */
    public static final String BUNDLE_FILE_PREFIX = "messages";

    private JQuickPackageUtils() {
    }

    /**
     * 提取 Class 所属包名，内部静态类取其外部类所在包。
     *
     * <p>Extract the package name of the given class; nested classes resolve to the enclosing package.</p>
     *
     * <p>之所以通过 {@code Class#getName()} 截取而不是直接使用 {@code Class#getPackage()}，
     * 是因为 {@code getPackage()} 在部分类加载器（例如自定义 ClassLoader、JDK8 的某些场景）下可能返回
     * {@code null}，而基于类名截取的方式始终稳定。</p>
     *
     * @param refClass 参考类，可以为 {@code null}
     * @return 包名；默认包或 {@code refClass} 为 {@code null} 时返回空字符串
     */
    public static String getPackageName(Class<?> refClass) {
        if (refClass == null) {
            return "";
        }
        String className = refClass.getName();
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            return className.substring(0, lastDot);
        }
        Package refPackage = refClass.getPackage();
        return refPackage == null ? "" : refPackage.getName();
    }

    /**
     * 获取父包名。
     *
     * <p>Get the parent package name, or an empty string when there is no parent.</p>
     *
     * @param packageName 当前包名，可以为 {@code null}
     * @return 父包名；顶级包（如 {@code com}）或默认包返回空字符串
     */
    public static String getParentPackage(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return "";
        }
        int lastDot = packageName.lastIndexOf('.');
        return lastDot < 0 ? "" : packageName.substring(0, lastDot);
    }

    /**
     * 从起始包逐级向上展开到顶级包，返回顺序为“就近优先”。
     *
     * <p>Expand the given package and all of its ancestors, nearest package first.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * List<String> packages = JQuickPackageUtils.expandToAncestors("com.github.paohaijiao.util");
     * // 结果：
     * // com.github.paohaijiao.util
     * // com.github.paohaijiao
     * // com.github
     * // com
     * }</pre>
     *
     * @param packageName 起始包名，可以为 {@code null}
     * @return 包名列表；起始包为空时返回空列表
     */
    public static List<String> expandToAncestors(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> packages = new ArrayList<>();
        String current = packageName;
        while (current != null && !current.isEmpty() && !packages.contains(current)) {
            packages.add(current);
            current = getParentPackage(current);
        }
        return packages;
    }

    /**
     * 将包名转换为该包内资源包的基名。
     *
     * <p>Convert a package name into the base name of the bundle shared by that package.</p>
     *
     * <p>Usage example / 使用示例：</p>
     * <pre>{@code
     * // 包 com.github.paohaijiao.util
     * // 资源 src/main/resources/com/github/paohaijiao/util/messages_zh_CN.properties
     * JQuickPackageUtils.toBundleBaseName("com.github.paohaijiao.util");
     * // 结果：com.github.paohaijiao.util.messages
     * }</pre>
     *
     * @param packageName 包名，可以为 {@code null}
     * @return 资源包基名；包名为空时返回空字符串，表示不做包级查找
     */
    public static String toBundleBaseName(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return "";
        }
        return packageName + "." + BUNDLE_FILE_PREFIX;
    }
}
