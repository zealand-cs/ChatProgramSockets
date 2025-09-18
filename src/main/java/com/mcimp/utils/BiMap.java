package com.mcimp.utils;

public interface BiMap<KV, VK> {
    void put(KV key, VK value);

    VK get(KV key);

    KV getByValue(VK value);

    void remove(KV key);

    void removeByValue(VK value);
}
