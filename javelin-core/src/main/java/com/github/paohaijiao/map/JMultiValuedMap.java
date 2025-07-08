package com.github.paohaijiao.map;

import java.util.*;
import java.util.function.BiConsumer;

public class JMultiValuedMap <K, V> implements Map<K, V> {

    private final LinkedList<Entry<K, V>> entries = new LinkedList<>();

    private final Map<K, List<Entry<K, V>>> keyIndex = new HashMap<>();

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyIndex.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K, V> entry : entries) {
            if (Objects.equals(entry.getValue(), value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        List<Entry<K, V>> entries = keyIndex.get(key);
        return entries != null && !entries.isEmpty() ?
                entries.get(entries.size() - 1).getValue() : null;
    }

    @Override
    public V put(K key, V value) {
        Entry<K, V> entry = new AbstractMap.SimpleEntry<>(key, value);
        entries.add(entry);
        keyIndex.computeIfAbsent(key, k -> new ArrayList<>()).add(entry);
        return null;
    }

    @Override
    public V remove(Object key) {
        List<Entry<K, V>> entriesForKey = keyIndex.get(key);
        if (entriesForKey == null || entriesForKey.isEmpty()) {
            return null;
        }

        Entry<K, V> lastEntry = entriesForKey.remove(entriesForKey.size() - 1);
        entries.remove(lastEntry);
        if (entriesForKey.isEmpty()) {
            keyIndex.remove(key);
        }
        return lastEntry.getValue();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        entries.clear();
        keyIndex.clear();
    }

    @Override
    public Set<K> keySet() {
        return keyIndex.keySet();
    }

    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>(entries.size());
        for (Entry<K, V> entry : entries) {
            values.add(entry.getValue());
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<>(entries);
    }

    public Collection<V> getAll(K key) {
        List<Entry<K, V>> entriesForKey = keyIndex.get(key);
        if (entriesForKey == null) {
            return Collections.emptyList();
        }

        List<V> values = new ArrayList<>(entriesForKey.size());
        for (Entry<K, V> entry : entriesForKey) {
            values.add(entry.getValue());
        }
        return values;
    }


    public V getFirst(K key) {
        List<Entry<K, V>> entriesForKey = keyIndex.get(key);
        return entriesForKey != null && !entriesForKey.isEmpty() ?
                entriesForKey.get(0).getValue() : null;
    }
    public int totalSize() {
        return entries.size();
    }

    public int count(K key) {
        List<Entry<K, V>> entriesForKey = keyIndex.get(key);
        return entriesForKey != null ? entriesForKey.size() : 0;
    }

    public void forEachEntry(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Entry<K, V> entry : entries) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }
}
