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
package com.github.paohaijiao.statement;


import com.github.paohaijiao.util.JReflectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class JQuickRow implements Map<String, Object> {

    private final Map<String, Object> data;


    /**
     * Creates an empty JRow with no associated table name.
     */
    public JQuickRow() {
        this.data = new HashMap<>();
    }

    /**
     * Creates a JRow initialized with the given map data.
     *
     * @param data the initial data for this row
     */
    public JQuickRow(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }




    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return data.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return data.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        data.putAll(m);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Set<String> keySet() {
        return data.keySet();
    }

    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
    }

    /**
     * Gets the value for the specified column as a String.
     *
     * @param columnName the column name
     * @return the String value, or null if the value is null
     * @throws ClassCastException if the value is not a String
     */
    public String getString(String columnName) {
        return (String) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as an Integer.
     *
     * @param columnName the column name
     * @return the Integer value, or null if the value is null
     * @throws ClassCastException if the value is not an Integer
     */
    public Integer getInt(String columnName) {
        return (Integer) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Long.
     *
     * @param columnName the column name
     * @return the Long value, or null if the value is null
     * @throws ClassCastException if the value is not a Long
     */
    public Long getLong(String columnName) {
        return (Long) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Double.
     *
     * @param columnName the column name
     * @return the Double value, or null if the value is null
     * @throws ClassCastException if the value is not a Double
     */
    public Double getDouble(String columnName) {
        return (Double) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Boolean.
     *
     * @param columnName the column name
     * @return the Boolean value, or null if the value is null
     * @throws ClassCastException if the value is not a Boolean
     */
    public Boolean getBoolean(String columnName) {
        return (Boolean) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Date.
     *
     * @param columnName the column name
     * @return the Date value, or null if the value is null
     * @throws ClassCastException if the value is not a Date
     */
    public Date getDate(String columnName) {
        return (Date) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Date.
     *
     * @param columnName the column name
     * @return the Date value, or null if the value is null
     * @throws ClassCastException if the value is not a Date
     */
    public Date getDate(String columnName, String format) {
        String value = (String) data.get(columnName);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(value);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the value for the specified column as a Object.
     *
     * @param columnName the column name
     * @return the Object value, or null if the value is null
     * @throws ClassCastException
     */
    public Object getObject(String columnName) {
        return (Object) data.get(columnName);
    }
    /**
     * Gets the value for the specified column with default value if null.
     *
     * @param columnName the column name
     * @param defaultValue the default value to return if the value is null
     * @return the value, or defaultValue if the value is null
     */
    public Object getObject(String columnName, Object defaultValue) {
        Object value = data.get(columnName);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets the value as String with default value.
     *
     * @param columnName the column name
     * @param defaultValue the default value
     * @return the String value, or defaultValue if null
     */
    public String getString(String columnName, String defaultValue) {
        String value = getString(columnName);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets the value as Integer with default value.
     *
     * @param columnName the column name
     * @param defaultValue the default value
     * @return the Integer value, or defaultValue if null
     */
    public Integer getInt(String columnName, Integer defaultValue) {
        Integer value = getInt(columnName);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets the value as Long with default value.
     *
     * @param columnName the column name
     * @param defaultValue the default value
     * @return the Long value, or defaultValue if null
     */
    public Long getLong(String columnName, Long defaultValue) {
        Long value = getLong(columnName);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets the value as Double with default value.
     *
     * @param columnName the column name
     * @param defaultValue the default value
     * @return the Double value, or defaultValue if null
     */
    public Double getDouble(String columnName, Double defaultValue) {
        Double value = getDouble(columnName);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets the value as Boolean with default value.
     *
     * @param columnName the column name
     * @param defaultValue the default value
     * @return the Boolean value, or defaultValue if null
     */
    public Boolean getBoolean(String columnName, Boolean defaultValue) {
        Boolean value = getBoolean(columnName);
        return value != null ? value : defaultValue;
    }
    /**
     * Gets the value as String, converting if necessary.
     *
     * @param columnName the column name
     * @return the String representation of the value, or null if the value is null
     */
    public String getStringConverted(String columnName) {
        Object value = data.get(columnName);
        return value != null ? value.toString() : null;
    }

    /**
     * Gets the value as Integer, converting if possible.
     *
     * @param columnName the column name
     * @return the Integer value, or null if conversion fails or value is null
     */
    public Integer getIntConverted(String columnName) {
        Object value = data.get(columnName);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the value as Long, converting if possible.
     *
     * @param columnName the column name
     * @return the Long value, or null if conversion fails or value is null
     */
    public Long getLongConverted(String columnName) {
        Object value = data.get(columnName);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the value as Double, converting if possible.
     *
     * @param columnName the column name
     * @return the Double value, or null if conversion fails or value is null
     */
    public Double getDoubleConverted(String columnName) {
        Object value = data.get(columnName);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the value as Boolean, converting if possible.
     *
     * @param columnName the column name
     * @return the Boolean value, or null if conversion fails or value is null
     */
    public Boolean getBooleanConverted(String columnName) {
        Object value = data.get(columnName);
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) {
            String str = (String) value;
            if ("true".equalsIgnoreCase(str) || "1".equals(str)) return true;
            if ("false".equalsIgnoreCase(str) || "0".equals(str)) return false;
        }
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        return null;
    }
    /**
     * Checks if the row contains the specified column.
     *
     * @param columnName the column name
     * @return true if the column exists
     */
    public boolean hasColumn(String columnName) {
        return data.containsKey(columnName);
    }

    /**
     * Checks if the specified column's value is null.
     *
     * @param columnName the column name
     * @return true if the column exists and its value is null
     */
    public boolean isNull(String columnName) {
        return data.containsKey(columnName) && data.get(columnName) == null;
    }
    /**
     * Gets multiple column values as a new map.
     *
     * @param columnNames the column names to retrieve
     * @return a map containing only the specified columns
     */
    public JQuickRow getColumns(String... columnNames) {
        JQuickRow result = new JQuickRow();
        for (String columnName : columnNames) {
            if (data.containsKey(columnName)) {
                result.put(columnName, data.get(columnName));
            }
        }
        return result;
    }
    /**
     * Removes multiple columns from this row.
     *
     * @param columnNames the column names to remove
     */
    public void removeColumns(String... columnNames) {
        for (String columnName : columnNames) {
            data.remove(columnName);
        }
    }

    /**
     * Gets the value for the specified column with type conversion.
     *
     * @param columnName the column name
     * @param type the target type class
     * @param <T> the type parameter
     * @return the converted value, or null if conversion fails
     */
    @SuppressWarnings("unchecked")
    public <T> T getAsConverted(String columnName, Class<T> type) {
        Object value = data.get(columnName);
        if (value == null) return null;
        if (type.isInstance(value)) return (T) value;
        if (type == String.class) return (T) value.toString();
        if (type == Integer.class) return (T) toInteger(value);
        if (type == Long.class) return (T) toLong(value);
        if (type == Double.class) return (T) toDouble(value);
        if (type == Float.class) return (T) toFloat(value);
        if (type == Boolean.class) return (T) toBoolean(value);
        if (type == Short.class) return (T) toShort(value);
        if (type == Byte.class) return (T) toByte(value);
        if (type == Date.class && value instanceof String) return (T) toDate((String) value);
        return null;
    }
    /**
     * Gets the value or throws an exception if the column does not exist.
     *
     * @param columnName the column name
     * @return the value
     * @throws IllegalArgumentException if the column does not exist
     */
    public Object require(String columnName) {
        if (!data.containsKey(columnName)) {
            throw new IllegalArgumentException("Required column '" + columnName + "' does not exist");
        }
        return data.get(columnName);
    }
    /**
     * Gets the value or throws an exception if the column does not exist or value is null.
     *
     * @param columnName the column name
     * @return the non-null value
     * @throws IllegalArgumentException if the column does not exist or value is null
     */
    public Object requireNonNull(String columnName) {
        Object value = require(columnName);
        if (value == null) {
            throw new IllegalArgumentException("Required column '" + columnName + "' has null value");
        }
        return value;
    }
    /**
     * Returns a builder for getting values with fallback options.
     *
     * @param columnName the column name
     * @return a ValueGetter builder
     */
    public ValueGetter getOr(String columnName) {
        return new ValueGetter(columnName);
    }
    /**
     * Merges another row into this row (overwrites existing keys).
     *
     * @param other the other row to merge from
     * @return this row for chaining
     */
    public JQuickRow merge(JQuickRow other) {
        if (other != null) {
            this.data.putAll(other.data);
        }
        return this;
    }
    public class ValueGetter {

        private final String columnName;

        private Object value;

        private ValueGetter(String columnName) {
            this.columnName = columnName;
            this.value = data.get(columnName);
        }

        /**
         * Provides a default value if the column value is null.
         */
        public Object orDefault(Object defaultValue) {
            return value != null ? value : defaultValue;
        }

        /**
         * Provides a value from another column if this column's value is null.
         */
        public Object orColumn(String otherColumnName) {
            if (value != null) return value;
            return data.get(otherColumnName);
        }

        /**
         * Provides a value from a supplier if this column's value is null.
         */
        public Object orSupply(java.util.function.Supplier<?> supplier) {
            return value != null ? value : supplier.get();
        }

        /**
         * Throws an exception if the value is null.
         */
        public Object orThrow() {
            if (value == null) {
                throw new IllegalStateException("Column '" + columnName + "' is null");
            }
            return value;
        }

        /**
         * Throws a custom exception if the value is null.
         */
        public Object orThrow(java.util.function.Supplier<? extends RuntimeException> exceptionSupplier) {
            if (value == null) {
                throw exceptionSupplier.get();
            }
            return value;
        }
    }
    /**
     * Merges another row into this row with a merge strategy for duplicate keys.
     *
     * @param other the other row to merge from
     * @param mergeFunction function to resolve conflicts (oldValue, newValue) -> mergedValue
     * @return this row for chaining
     */
    public JQuickRow merge(JQuickRow other, java.util.function.BinaryOperator<Object> mergeFunction) {
        if (other == null) return this;
        for (Map.Entry<String, Object> entry : other.data.entrySet()) {
            String key = entry.getKey();
            Object newValue = entry.getValue();
            if (this.data.containsKey(key)) {
                Object oldValue = this.data.get(key);
                this.data.put(key, mergeFunction.apply(oldValue, newValue));
            } else {
                this.data.put(key, newValue);
            }
        }
        return this;
    }
    /**
     * Creates a new row with only the specified columns.
     *
     * @param columnNames columns to keep
     * @return a new JQuickRow with only the specified columns
     */
    public JQuickRow select(String... columnNames) {
        Map<String, Object> selected = new HashMap<>();
        for (String columnName : columnNames) {
            if (data.containsKey(columnName)) {
                selected.put(columnName, data.get(columnName));
            }
        }
        return new JQuickRow(selected);
    }
    /**
     * Creates a new row without the specified columns.
     *
     * @param columnNames columns to exclude
     * @return a new JQuickRow without the specified columns
     */
    public JQuickRow except(String... columnNames) {
        Map<String, Object> filtered = new HashMap<>(data);
        for (String columnName : columnNames) {
            filtered.remove(columnName);
        }
        return new JQuickRow(filtered);
    }
    /**
     * Gets the type of the value for the specified column.
     *
     * @param columnName the column name
     * @return the Class of the value, or null if column doesn't exist or value is null
     */
    public Class<?> getColumnType(String columnName) {
        Object value = data.get(columnName);
        return value != null ? value.getClass() : null;
    }
    /**
     * Checks if the column value is a number.
     *
     * @param columnName the column name
     * @return true if the value is a Number
     */
    public boolean isNumber(String columnName) {
        Object value = data.get(columnName);
        return value instanceof Number;
    }
    /**
     * Checks if the column value is a string.
     *
     * @param columnName the column name
     * @return true if the value is a String
     */
    public boolean isString(String columnName) {
        Object value = data.get(columnName);
        return value instanceof String;
    }

    /**
     * Checks if the column value is a date.
     *
     * @param columnName the column name
     * @return true if the value is a Date
     */
    public boolean isDate(String columnName) {
        Object value = data.get(columnName);
        return value instanceof Date;
    }

    /**
     * Checks if the column value is a boolean.
     *
     * @param columnName the column name
     * @return true if the value is a Boolean
     */
    public boolean isBoolean(String columnName) {
        Object value = data.get(columnName);
        return value instanceof Boolean;
    }
    /**
     * Gets the value as Date with multiple format attempts.
     *
     * @param columnName the column name
     * @param formats the possible date formats
     * @return the Date value, or null if none of the formats work
     */
    public Date getDate(String columnName, String... formats) {
        String value = (String) data.get(columnName);
        if (value == null) return null;

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(value);
            } catch (Exception e) {
                // continue to next format
            }
        }
        return null;
    }

    /**
     * Gets the value as LocalDate (requires Java 8+).
     *
     * @param columnName the column name
     * @return the LocalDate value, or null
     */
    public java.time.LocalDate getLocalDate(String columnName) {
        Object value = data.get(columnName);
        if (value instanceof java.time.LocalDate) return (java.time.LocalDate) value;
        if (value instanceof Date) return ((Date) value).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        if (value instanceof String) {
            try {
                return java.time.LocalDate.parse((String) value);
            } catch (java.time.format.DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the value as LocalDateTime (requires Java 8+).
     *
     * @param columnName the column name
     * @return the LocalDateTime value, or null
     */
    public java.time.LocalDateTime getLocalDateTime(String columnName) {
        Object value = data.get(columnName);
        if (value instanceof java.time.LocalDateTime) return (java.time.LocalDateTime) value;
        if (value instanceof Date) return ((Date) value).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        if (value instanceof String) {
            try {
                return java.time.LocalDateTime.parse((String) value);
            } catch (java.time.format.DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }


    private Integer toInteger(Object value) {
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try { return Integer.parseInt((String) value); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Long toLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try { return Long.parseLong((String) value); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Double toDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try { return Double.parseDouble((String) value); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Float toFloat(Object value) {
        if (value instanceof Number) return ((Number) value).floatValue();
        if (value instanceof String) {
            try { return Float.parseFloat((String) value); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) {
            String str = (String) value;
            if ("true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str) || "1".equals(str)) return true;
            if ("false".equalsIgnoreCase(str) || "no".equalsIgnoreCase(str) || "0".equals(str)) return false;
        }
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        return null;
    }

    private Short toShort(Object value) {
        if (value instanceof Number) return ((Number) value).shortValue();
        if (value instanceof String) {
            try { return Short.parseShort((String) value); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Byte toByte(Object value) {
        if (value instanceof Number) return ((Number) value).byteValue();
        if (value instanceof String) {
            try { return Byte.parseByte((String) value); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private Date toDate(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(value);
        } catch (Exception e1) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(value);
            } catch (Exception e2) {
                return null;
            }
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JQuickRow jRow = (JQuickRow) o;
        return data.equals(jRow.data) ;
    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JQuickRow{" + " data=" + data + '}';
    }

    public static List<JQuickRow> toRows(List<?> list) {
        List<JQuickRow> mapList = new ArrayList();
        for(Object object : list) {
            if (object instanceof Map) {
                mapList.add((new JQuickRow((Map)object)));
            } else if (object instanceof Object) {
                Map<String, Object> fieldAndValue = JReflectionUtils.getFieldAndFieldValueByObject(object);
                mapList.add(new JQuickRow(fieldAndValue));
            }
        }
        return mapList;
    }
    /**
     * Converts this row to a map.
     *
     * @return a new map containing all data
     */
    public Map<String, Object> toMap() {
        return new HashMap<>(data);
    }

    /**
     * Converts this row to a bean of the specified class.
     *
     * @param clazz the target class
     * @param <T> the type parameter
     * @return the converted bean, or null if conversion fails
     */
    public <T> T toBean(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                JReflectionUtils.setFieldValue(instance, entry.getKey(), entry.getValue());
            }
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets value as specific type, returns null if type mismatch.
     *
     * @param columnName the column name
     * @param type the expected type
     * @param <T> the type parameter
     * @return the value cast to type T, or null
     */
    @SuppressWarnings("unchecked")
    public <T> T getAs(String columnName, Class<T> type) {
        Object value = data.get(columnName);
        if (value == null) return null;
        if (type.isInstance(value)) return (T) value;
        return null;
    }
}

