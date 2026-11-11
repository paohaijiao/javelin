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
package com.github.paohaijiao.type;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;

/**
 * SPI 扩展测试用的百分比转换器（高优先级）。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/11/11
 */
@Priority(PriorityConstants.APPLICATION_MEDIUM)
public class JQuickPercentTypeConverter implements JQuickTypeConverter {

    @Override
    public boolean supports(Class<?> targetType) {
        return targetType == JQuickPercentValue.class;
    }

    @Override
    public Object convert(Object value, Class<?> targetType) {
        return JQuickPercentValue.parse(value.toString());
    }
}
