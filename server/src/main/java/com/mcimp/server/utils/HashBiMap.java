package com.mcimp.server.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashBiMap<KV, VK> implements BiMap<KV, VK> {
    private Map<KV, VK> versed;
    private Map<VK, KV> inversed;

    public HashBiMap() {
        versed = new HashMap<>();
        inversed = new HashMap<>();
    }

    @Override
    public void put(KV key, VK value) {
        versed.put(key, value);
        inversed.put(value, key);
    }

    @Override
    public VK get(KV key) {
        return versed.get(key);
    }

    @Override
    public KV getByValue(VK value) {
        return inversed.get(value);
    }

    @Override
    public void remove(KV key) {
        VK val = versed.remove(key);
        inversed.remove(val);
    }

    @Override
    public void removeByValue(VK value) {
        KV key = inversed.remove(value);
        versed.remove(key);
    }

    @Override
    public Set<KV> keySet() {
        return versed.keySet();
    }

    @Override
    public Set<VK> valueSet() {
        return inversed.keySet();
    }
}
