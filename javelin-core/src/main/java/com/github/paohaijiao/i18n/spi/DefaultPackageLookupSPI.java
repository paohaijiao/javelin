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

import com.github.paohaijiao.i18n.JQuickPackageUtils;

import java.util.List;

/**
 * 默认包回溯遍历策略：就近优先，逐级向上回溯到顶级包。
 * Default package lookup strategy: nearest package first, then walk up to the top-level package.
 *
 * <p>与 Kettle {@code Messages} 的查找顺序一致：先看当前包，命中即返回；未命中才继续向上。</p>
 *
 * <p>Usage example / 使用示例：</p>
 * <pre>{@code
 * PackageLookupSPI spi = new DefaultPackageLookupSPI();
 * spi.resolvePackages("com.github.paohaijiao.util");
 * // 结果：com.github.paohaijiao.util, com.github.paohaijiao, com.github, com
 * }</pre>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/08/24
 */
public class DefaultPackageLookupSPI implements PackageLookupSPI {

    /**
     * {@inheritDoc}
     *
     * <p>返回起始包及其全部祖先包，顺序为就近优先。</p>
     */
    @Override
    public List<String> resolvePackages(String startPackage) {
        return JQuickPackageUtils.expandToAncestors(startPackage);
    }
}
