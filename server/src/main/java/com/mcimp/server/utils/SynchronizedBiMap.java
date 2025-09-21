package com.mcimp.server.utils;

import java.util.Set;

public class SynchronizedBiMap<KV, VK> implements BiMap<KV, VK> {
    private BiMap<KV, VK> bimap;

    public SynchronizedBiMap(BiMap<KV, VK> bimap) {
        this.bimap = bimap;
    }

    @Override
    public synchronized void put(KV key, VK value) {
        bimap.put(key, value);
    }

    @Override
    public synchronized VK get(KV key) {
        return bimap.get(key);
    }

    @Override
    public synchronized KV getByValue(VK value) {
        return bimap.getByValue(value);
    }

    @Override
    public synchronized void remove(KV key) {
        bimap.remove(key);
    }

    @Override
    public synchronized void removeByValue(VK value) {
        bimap.removeByValue(value);
    }

    @Override
    public synchronized Set<KV> keySet() {
        return bimap.keySet();
    }
    
    @Override
    public synchronized Set<VK> valueSet() {
        return bimap.valueSet();
    }
}
