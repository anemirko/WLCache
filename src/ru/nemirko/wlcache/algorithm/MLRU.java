package ru.nemirko.wlcache.algorithm;

import javafx.util.Pair;
import ru.nemirko.wlcache.bucket.Bucket;

import java.io.Serializable;


public class MLRU<K,V extends Serializable> extends AbstractAlgorithm<K,V> {

    private LRU<K,V> segment1;
    private LRU<K,V> segment2;

    public MLRU(Bucket<K, V> bucket1, Bucket<K, V> bucket2) {
        segment1 = new LRU<>(bucket1);
        segment2 = new LRU<>(bucket2);
    }

    @Override
    public void evictAll() {
        segment1.evictAll();
        segment2.evictAll();
    }

    @Override
    public Pair<K, V> put(K key, V value) {
        if (segment1.containsKey(key) || segment2.containsKey(key)) {
            if (segment1.containsKey(key)) {
                return segment1.put(key, value);
            } else {
                return segment2.put(key, value);
            }
        }
        if (segment2.isFull()) {
            return segment1.put(key, value);
        } else {
            if (segment1.isFull()) {
                //Pair<K, V> pair = segment1.evictFirst();
                return segment2.put(key, value);
            }
            return segment1.put(key, value);
        }

    }

    @Override
    public V evict(K key) {
        if (segment1.containsKey(key)) {
            return segment1.evict(key);
        }
        return segment2.evict(key);
    }

    @Override
    public V get(K key) {
        if (segment1.containsKey(key) || segment2.containsKey(key)) {
            cacheHit++;
            if (segment1.containsKey(key)) {
                V value = segment1.get(key);
                segment1.evict(key);
                Pair<K,V> from2to1 = segment2.put(key, value);
                if (from2to1 != null) {
                    segment1.put(from2to1.getKey(), from2to1.getValue());
                }
                return value;
            } else {
                return segment2.get(key);
            }
        } else {
            cacheMiss++;
        }
        return null;
    }

    @Override
    public boolean isFull() {
        return (segment1.isFull() && segment2.isFull());
    }

    @Override
    public int cacheHit() {
        return segment1.cacheHit + segment2.cacheHit;
    }

    @Override
    public int cacheMiss() {
        return segment1.cacheMiss + segment2.cacheMiss;
    }

    @Override
    public boolean containsKey(K key) {
        return (segment1.containsKey(key) || segment2.containsKey(key));
    }
}
