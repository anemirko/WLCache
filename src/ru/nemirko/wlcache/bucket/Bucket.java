package ru.nemirko.wlcache.bucket;

public interface Bucket<K,V> {

    int size();

    int maxSize();

    boolean isEmpty();

    boolean isFull();

    boolean containsKey(K key);

    void clear();

    V get(K key);

    V put(K key, V value);

    V remove(K key);
}
