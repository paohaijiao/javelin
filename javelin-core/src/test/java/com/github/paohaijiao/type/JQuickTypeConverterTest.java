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

import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.statement.asm.JQuickRowToBeanConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * 类型转换 SPI 扩展点单元测试。
 *
 * <p>验证 {@link JQuickTypeConverter} 能按优先级加载，并被
 * {@link JQuickRowToBeanConverter#coerce(Object, Class, boolean)} 在两条转换路径中调用。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/11/11
 */
public class JQuickTypeConverterTest {

    /**
     * 含自定义类型属性的 Bean。
     */
    public static class PercentBean {

        private JQuickPercentValue rate;

        public JQuickPercentValue getRate() {
            return rate;
        }

        public void setRate(JQuickPercentValue rate) {
            this.rate = rate;
        }
    }

    private static List<JQuickRow> rows(int size) {
        List<JQuickRow> rows = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JQuickRow row = new JQuickRow();
            row.put("rate", "87.5%");
            rows.add(row);
        }
        return rows;
    }

    @Test
    @DisplayName("按优先级加载转换器")
    void managerLoadsConverterByPriority() {
        JQuickTypeConverter converter = JQuickTypeConverterManager.getConverter(JQuickPercentValue.class);
        assertNotNull(converter);
        assertSame(JQuickPercentTypeConverter.class, converter.getClass());
    }

    @Test
    @DisplayName("coerce 对自定义类型使用 SPI 转换器")
    void coerceUsesSpiConverter() {
        Object converted = JQuickRowToBeanConverter.coerce("87.5%", JQuickPercentValue.class, false);
        assertEquals(new JQuickPercentValue(87.5D), converted);
    }

    @Test
    @DisplayName("反射路径使用 SPI 转换器")
    void toBeanListUsesSpiConverterWithReflectionPath() {
        List<PercentBean> beans = JQuickRow.toBeanList(rows(3), PercentBean.class);
        assertEquals(3, beans.size());
        assertEquals(new JQuickPercentValue(87.5D), beans.get(0).getRate());
    }

    @Test
    @DisplayName("ASM 路径使用 SPI 转换器")
    void toBeanListUsesSpiConverterWithAsmPath() {
        List<PercentBean> beans = JQuickRow.toBeanList(rows(12), PercentBean.class);
        assertEquals(12, beans.size());
        for (PercentBean bean : beans) {
            assertEquals(new JQuickPercentValue(87.5D), bean.getRate());
        }
    }

    @Test
    @DisplayName("重新加载后转换器仍可用")
    void reloadKeepsConvertersAvailable() {
        JQuickTypeConverterManager.reload();
        JQuickTypeConverter converter = JQuickTypeConverterManager.getConverter(JQuickPercentValue.class);
        assertNotNull(converter);
        assertSame(JQuickPercentTypeConverter.class, converter.getClass());
    }
}
