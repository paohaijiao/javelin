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

import java.util.List;

/**
 * 包回溯遍历策略扩展点 / Package backtracking lookup strategy SPI。
 *
 * <p>该接口决定 {@code Messages} 在查找资源包时，按什么顺序、遍历哪些包。
 * 默认实现 {@link DefaultPackageLookupSPI} 采用“就近优先 + 逐级向上回溯”的 Kettle 风格策略。
 * 自定义实现可以修改遍历顺序，或只返回固定包集合。</p>
 *
 * <p>注意：当 {@code I18nConfig#setEnablePackageLookup(boolean)} 被设置为 {@code false} 时，
 * 该扩展点不会生效，{@code Messages} 只查询 refClass 所在包，随后直接查询全局兜底 bundle。</p>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * // 自定义策略：父包优先，便于统一覆盖子包文案
 * public class ParentFirstLookupSPI implements PackageLookupSPI {
 *     @Override
 *     public List<String> resolvePackages(String startPackage) {
 *         List<String> packages = new ArrayList<>();
 *         String parent = JQuickPackageUtils.getParentPackage(startPackage);
 *         if (!parent.isEmpty()) {
 *             packages.add(parent);
 *         }
 *         packages.add(startPackage);
 *         return packages;
 *     }
 * }
 *
 * // 方式一：显式注册（优先级最高）
 * I18nConfig.setPackageLookupSPI(new ParentFirstLookupSPI());
 *
 * // 方式二：SPI 自动发现，在
 * // src/main/resources/META-INF/services/com.github.paohaijiao.i18n.spi.PackageLookupSPI
 * // 中写入实现类全限定名即可
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 */
public interface PackageLookupSPI {

    /**
     * 解析需要依次查找资源包的包名列表，返回顺序即查找顺序。
     *
     * <p>Resolve the ordered package names to look up resource bundles.</p>
     *
     * @param startPackage 起始包名，即 refClass 所在包，可能为空字符串（默认包）
     * @return 包名列表，按查找优先级排列；返回 {@code null} 或空列表时退化为只查询起始包
     */
    List<String> resolvePackages(String startPackage);
}
