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
package com.github.paohaijiao.store;

public class JStoreItem<T> {
    private final String id;
    private T value;
    private final Class<T> type;

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
