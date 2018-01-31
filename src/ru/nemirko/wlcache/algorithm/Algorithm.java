package ru.nemirko.wlcache.algorithm;

import javafx.util.Pair;

public interface Algorithm<K,V> {

    void evictAll();

    Pair<K,V> put(K key, V value);

    V evict(K key);

    V get(K key);

    boolean isFull();

    boolean containsKey(K key);

    int cacheHit();

    int cacheMiss();
}
