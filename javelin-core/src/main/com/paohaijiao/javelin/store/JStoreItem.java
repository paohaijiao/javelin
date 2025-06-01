package com.paohaijiao.javelin.store;

public class JStoreItem<T> {
    private final String id;
    private T value;
    private final Class<T> type;

    /**
     * 构造函数
     * @param id 变量唯一标识符
     * @param value 存储的值
     * @param type 值的类型
     */
    public JStoreItem(String id, T value, Class<T> type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    /**
     * 获取变量ID
     * @return 变量ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取存储的值
     * @return 存储的值
     */
    public T getValue() {
        return value;
    }

    /**
     * 获取值的类型
     * @return 值的Class对象
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * 设置新的值
     * @param newValue 新值
     * @throws IllegalArgumentException 如果新值类型不匹配
     */
    public void setValue(T newValue) {
        if (newValue != null && !type.isInstance(newValue)) {
            throw new IllegalArgumentException("Type mismatch. Expected: " + type.getName());
        }
        this.value = newValue;
    }

    @Override
    public String toString() {
        return "JStoreItem{" + "id='" + id + '\'' + ", value=" + value + ", type=" + type.getSimpleName() + '}';
    }
}
