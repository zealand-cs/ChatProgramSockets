package com.mcimp.utils;

import java.util.HashMap;
import java.util.Map;

public class HashBiMap<K, V> implements BiMap<K, V> {
    private Map<K, V> versed;
    private Map<V, K> inversed;

    public HashBiMap() {
        versed = new HashMap<>();
        inversed = new HashMap<>();
    }

    @Override
    public void put(K key, V value) {
        versed.put(key, value);
        inversed.put(value, key);
    }

    @Override
    public V get(K key) {
        return versed.get(key);
    }

    @Override
    public K getByValue(V value) {
        return inversed.get(value);
    }

    @Override
    public void remove(K key) {
        var val = versed.remove(key);
        inversed.remove(val);
    }

    @Override
    public void removeByValue(V value) {
        var key = inversed.remove(value);
        versed.remove(key);
    }
}
