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
package com.github.paohaijiao.statement.asm;

import com.github.paohaijiao.statement.JQuickRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link JQuickRowToBeanConverter#coerce(Object, Class, boolean)} 单元测试。
 *
 * <p>覆盖数据源与 Bean 属性类型不一致时的转换规则，以及 toBeanList 的
 * 反射路径（少于 10 行）和 ASM 路径（大于等于 10 行）的一致性。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/11/11
 */
public class JQuickRowToBeanConverterTest {

    /**
     * 模拟报表统计行对应的 Bean（字段类型与数据源类型故意不一致）。
     */
    public static class StatisticsBean {

        private String deptName;

        private Long deptId;

        private Integer compiledCatalogTotal;

        private String claimRatio;

        private String type;

        private String level;

        private Date createDate;

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public Long getDeptId() {
            return deptId;
        }

        public void setDeptId(Long deptId) {
            this.deptId = deptId;
        }

        public Integer getCompiledCatalogTotal() {
            return compiledCatalogTotal;
        }

        public void setCompiledCatalogTotal(Integer compiledCatalogTotal) {
            this.compiledCatalogTotal = compiledCatalogTotal;
        }

        public String getClaimRatio() {
            return claimRatio;
        }

        public void setClaimRatio(String claimRatio) {
            this.claimRatio = claimRatio;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }
    }

    public enum Status {
        ACTIVE,
        DISABLED
    }

    private static Object coerce(Object value, Class<?> targetType) {
        return JQuickRowToBeanConverter.coerce(value, targetType, targetType.isPrimitive());
    }

    private static JQuickRow statisticsRow() {
        JQuickRow row = new JQuickRow();
        row.put("deptName", "省人力资源社会保障厅");
        row.put("deptId", 5);
        row.put("compiledCatalogTotal", 233);
        row.put("claimRatio", "90.6%");
        row.put("type", 2);
        row.put("level", 1);
        row.put("createDate", "2026-11-26T16:00:00Z");
        return row;
    }

    private static List<JQuickRow> statisticsRows(int size) {
        List<JQuickRow> rows = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            rows.add(statisticsRow());
        }
        return rows;
    }

    @Test
    @DisplayName("Integer 转 String")
    void coerceIntegerToString() {
        assertEquals("2", coerce(2, String.class));
        assertEquals("5", coerce(5L, String.class));
        assertEquals("true", coerce(true, String.class));
    }

    @Test
    @DisplayName("Integer 转其他数值类型")
    void coerceIntegerToOtherNumberTypes() {
        assertEquals(Long.valueOf(5L), coerce(5, Long.class));
        assertEquals(Double.valueOf(5D), coerce(5, Double.class));
        assertEquals(Float.valueOf(5F), coerce(5, Float.class));
        assertEquals(Short.valueOf((short) 5), coerce(5, Short.class));
        assertEquals(Byte.valueOf((byte) 5), coerce(5, Byte.class));
    }

    @Test
    @DisplayName("数值字符串转数值类型")
    void coerceNumberStringToNumberTypes() {
        assertEquals(Integer.valueOf(123), coerce("123", Integer.class));
        assertEquals(Long.valueOf(123L), coerce(" 123 ", Long.class));
        assertEquals(Double.valueOf(90.6D), coerce("90.6", Double.class));
        assertEquals(Float.valueOf(90.6F), coerce("90.6", Float.class));
        assertEquals(new BigDecimal("90.6"), coerce("90.6", BigDecimal.class));
        assertEquals(new BigInteger("123"), coerce("123", BigInteger.class));
    }

    @Test
    @DisplayName("类型已经匹配时返回原对象")
    void coerceSameTypeKeepsInstance() {
        String text = "90.6%";
        assertSame(text, coerce(text, String.class));
        Integer number = 5;
        assertSame(number, coerce(number, Integer.class));
        Date date = new Date();
        assertSame(date, coerce(date, Date.class));
    }

    @Test
    @DisplayName("字符串转 Boolean")
    void coerceStringToBoolean() {
        assertTrue((Boolean) coerce("true", Boolean.class));
        assertTrue((Boolean) coerce("TRUE", Boolean.class));
        assertTrue((Boolean) coerce("1", Boolean.class));
        assertTrue((Boolean) coerce("yes", Boolean.class));
        assertTrue((Boolean) coerce("on", Boolean.class));
        assertFalse((Boolean) coerce("false", Boolean.class));
        assertFalse((Boolean) coerce("0", Boolean.class));
        assertFalse((Boolean) coerce("no", Boolean.class));
    }

    @Test
    @DisplayName("数值转 Boolean / Character")
    void coerceNumberToBooleanAndCharacter() {
        assertTrue((Boolean) coerce(1, Boolean.class));
        assertFalse((Boolean) coerce(0, Boolean.class));
        assertEquals(Character.valueOf('A'), coerce(65, Character.class));
        assertEquals(Character.valueOf('A'), coerce("A", Character.class));
    }

    @Test
    @DisplayName("ISO-8601 字符串转 Date")
    void coerceIsoStringToDate() {
        Date date = (Date) coerce("2026-11-26T16:00:00Z", Date.class);
        assertEquals(Instant.parse("2026-11-26T16:00:00Z").toEpochMilli(), date.getTime());

        Date offsetDate = (Date) coerce("2026-11-26T16:00:00+08:00", Date.class);
        assertEquals(Instant.parse("2026-11-26T08:00:00Z").toEpochMilli(), offsetDate.getTime());
    }

    @Test
    @DisplayName("普通日期格式字符串转 Date")
    void coercePlainDateStringToDate() throws Exception {
        Date date = (Date) coerce("2026-11-26 16:00:00", Date.class);
        Date expected = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2026-11-26 16:00:00");
        assertEquals(expected.getTime(), date.getTime());
    }

    @Test
    @DisplayName("java.time 类型转 Date")
    void coerceTemporalToDate() {
        Date fromInstant = (Date) coerce(Instant.parse("2026-11-26T16:00:00Z"), Date.class);
        assertEquals(Instant.parse("2026-11-26T16:00:00Z").toEpochMilli(), fromInstant.getTime());

        Date fromLocalDate = (Date) coerce(LocalDate.of(2026, 11, 26), Date.class);
        assertEquals(Date.from(LocalDate.of(2026, 11, 26)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime(), fromLocalDate.getTime());

        Date fromLocalDateTime = (Date) coerce(LocalDateTime.of(2026, 11, 26, 16, 0, 0), Date.class);
        assertEquals(Date.from(LocalDateTime.of(2026, 11, 26, 16, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant()).getTime(), fromLocalDateTime.getTime());

        Date fromOffset = (Date) coerce(OffsetDateTime.parse("2026-11-26T16:00:00+08:00"), Date.class);
        assertEquals(Instant.parse("2026-11-26T08:00:00Z").toEpochMilli(), fromOffset.getTime());
    }

    @Test
    @DisplayName("Date 转 java.sql.Timestamp")
    void coerceDateToTimestamp() {
        Date date = new Date(1_800_000_000_000L);
        Object value = coerce(date, Timestamp.class);
        assertTrue(value instanceof Timestamp);
        assertEquals(date.getTime(), ((Timestamp) value).getTime());
    }

    @Test
    @DisplayName("字符串转枚举（忽略大小写）")
    void coerceStringToEnum() {
        assertSame(Status.ACTIVE, coerce("ACTIVE", Status.class));
        assertSame(Status.ACTIVE, coerce("active", Status.class));
        assertSame(Status.DISABLED, coerce("Disabled", Status.class));
    }

    @Test
    @DisplayName("无法解析的日期抛出明确异常")
    void coerceInvalidDateThrows() {
        assertThrows(IllegalArgumentException.class, () -> coerce("not-a-date", Date.class));
    }

    @Test
    @DisplayName("null 转引用类型保持 null")
    void coerceNullToReferenceType() {
        assertNull(coerce(null, String.class));
        assertNull(coerce(null, Date.class));
        assertNull(coerce(null, Long.class));
    }

    @Test
    @DisplayName("null 转基本类型返回类型默认值")
    void coerceNullToPrimitiveType() {
        assertEquals(Integer.valueOf(0), JQuickRowToBeanConverter.coerce(null, Integer.class, true));
        assertEquals(Long.valueOf(0L), JQuickRowToBeanConverter.coerce(null, Long.class, true));
        assertEquals(Double.valueOf(0D), JQuickRowToBeanConverter.coerce(null, Double.class, true));
        assertEquals(Boolean.FALSE, JQuickRowToBeanConverter.coerce(null, Boolean.class, true));
        assertEquals(Character.valueOf('\0'), JQuickRowToBeanConverter.coerce(null, Character.class, true));
    }

    @Test
    @DisplayName("少于 10 行走反射路径：类型不一致可正确转换")
    void toBeanListWithReflectionPath() {
        List<StatisticsBean> result = JQuickRow.toBeanList(statisticsRows(3), StatisticsBean.class);

        assertEquals(3, result.size());
        StatisticsBean bean = result.get(0);
        assertEquals("省人力资源社会保障厅", bean.getDeptName());
        assertEquals(Long.valueOf(5L), bean.getDeptId());
        assertEquals(Integer.valueOf(233), bean.getCompiledCatalogTotal());
        assertEquals("90.6%", bean.getClaimRatio());
        assertEquals("2", bean.getType());
        assertEquals("1", bean.getLevel());
        assertEquals(Instant.parse("2026-11-26T16:00:00Z").toEpochMilli(), bean.getCreateDate().getTime());
    }

    @Test
    @DisplayName("不少于 10 行走 ASM 路径：类型不一致不再抛 ClassCastException")
    void toBeanListWithAsmPath() {
        List<StatisticsBean> result = JQuickRow.toBeanList(statisticsRows(12), StatisticsBean.class);
        assertEquals(12, result.size());
        for (StatisticsBean bean : result) {
            assertEquals(Long.valueOf(5L), bean.getDeptId());
            assertEquals("2", bean.getType());
            assertEquals("1", bean.getLevel());
            assertEquals(Instant.parse("2026-11-26T16:00:00Z").toEpochMilli(), bean.getCreateDate().getTime());
        }
    }
}
