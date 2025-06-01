package com.paohaijiao.javelin.param;

import java.util.*;
import java.util.Map;

public class ContextParams implements Map<String, Object> {

    private final Map<String, Object> constants;

    private final Map<String, Object> variables;

    public ContextParams() {
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
