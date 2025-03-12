package org.example.adventuretime.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LruCache<K, V> {
    private final Map<K, V> cache;
    private final ReentrantLock lock = new ReentrantLock();

    public LruCache(int maxSize) {
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    public V get(K key) {
        lock.lock();
        try {
            return cache.get(key);
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            cache.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public void remove(K key) {
        lock.lock();
        try {
            cache.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public Collection<V> getAll() {
        lock.lock();
        try {
            return new ArrayList<>(cache.values());
        } finally {
            lock.unlock();
        }
    }
}