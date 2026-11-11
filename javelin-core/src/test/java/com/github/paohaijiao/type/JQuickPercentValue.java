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

/**
 * SPI 扩展测试用的自定义值类型（百分比）。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/11/11
 */
public final class JQuickPercentValue {

    private final double value;

    public JQuickPercentValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static JQuickPercentValue parse(String text) {
        String normalized = text.trim();
        if (normalized.endsWith("%")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return new JQuickPercentValue(Double.parseDouble(normalized));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JQuickPercentValue)) {
            return false;
        }
        return Double.compare(value, ((JQuickPercentValue) obj).value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(value).hashCode();
    }

    @Override
    public String toString() {
        return "JQuickPercentValue{" + value + '}';
    }
}
