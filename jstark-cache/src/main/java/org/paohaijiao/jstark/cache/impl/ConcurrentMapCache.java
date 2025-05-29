package org.paohaijiao.jstark.cache.impl;

import org.paohaijiao.jstark.cache.Cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapCache implements Cache {
    private final String name;
    private final ConcurrentMap<Object, Object> store;
    private final boolean allowNullValues;

    public ConcurrentMapCache(String name) {
        this(name, new ConcurrentHashMap<>(256), true);
    }

    public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues) {
        this.name = name;
        this.store = store;
        this.allowNullValues = allowNullValues;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.store;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object value = this.store.get(key);
        return toValueWrapper(value);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object value = this.store.get(key);
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T) value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = this.store.get(key);
        if (value != null) {
            return (T) value;
        }

        return (T) this.store.computeIfAbsent(key, k -> {
            try {
                return valueLoader.call();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public void put(Object key, Object value) {
        this.store.put(key, toStoreValue(value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object existing = this.store.putIfAbsent(key, toStoreValue(value));
        return toValueWrapper(existing);
    }

    @Override
    public void evict(Object key) {
        this.store.remove(key);
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    protected Object toStoreValue(Object userValue) {
        if (!this.allowNullValues && userValue == null) {
            throw new IllegalArgumentException("Cache '" + getName() + "' does not allow null values");
        }
        return userValue;
    }

    private ValueWrapper toValueWrapper(Object value) {
        return (value != null ? () -> value : null);
    }
}
