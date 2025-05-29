package org.paohaijiao.jstark.cache;

import java.util.concurrent.Callable;

public interface Cache {

    String getName();

    Object getNativeCache();

    ValueWrapper get(Object key);


    <T> T get(Object key, Class<T> type);

    <T> T get(Object key, Callable<T> valueLoader);


    void put(Object key, Object value);

    ValueWrapper putIfAbsent(Object key, Object value);

    void evict(Object key);

    void clear();

    interface ValueWrapper {
        Object get();
    }
}
