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
package com.github.paohaijiao.param;

import java.util.*;

public class JContext implements Map<String, Object> {

    private final Map<String, Object> constants;

    private final Map<String, Object> variables;

    public JContext() {
        this.constants = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public void addConstant(String key, Object value) {
        if (constants.containsKey(key)) {
            throw new IllegalStateException("variable '" + key + "' has already been defined");
        }
        constants.put(key, value);
    }

    public Map<String, Object> getConstants() {
        return Collections.unmodifiableMap(constants);
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
    @Override
    public int size() {
        return constants.size() + variables.size();
    }

    @Override
    public boolean isEmpty() {
        return constants.isEmpty() && variables.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return constants.containsKey(key) || variables.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return constants.containsValue(value) || variables.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        Object value = constants.get(key);
        return value != null ? value : variables.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        if (constants.containsKey(key)) {
            throw new UnsupportedOperationException("alread  exists" + key + "'");
        }
        return variables.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        if (constants.containsKey(key)) {
            throw new UnsupportedOperationException("can not delete '" + key + "'");
        }
        return variables.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Entry<? extends String, ?> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        variables.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>(constants.keySet());
        keys.addAll(variables.keySet());
        return Collections.unmodifiableSet(keys);
    }

    @Override
    public Collection<Object> values() {
        Collection<Object> values = new ArrayList<>(constants.values());
        values.addAll(variables.values());
        return Collections.unmodifiableCollection(values);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entries = new HashSet<>(constants.entrySet());
        entries.addAll(variables.entrySet());
        return Collections.unmodifiableSet(entries);
    }


    public Object getOrDefault(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }


    public <T> T get(String key, Class<T> type) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("parameter '" + key + "' not match ,expect: " +
                    type.getName() + "，actual: " + value.getClass().getName());
        }
        return type.cast(value);
    }

    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        T value = get(key, type);
        return value != null ? value : defaultValue;
    }

    @Override
    public String toString() {
        return "ContextParams{" +
                "constants=" + constants +
                ", variables=" + variables +
                '}';
    }
}
