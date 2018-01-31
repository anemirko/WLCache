package ru.nemirko.wlcache.algorithm;

import javafx.util.Pair;
import ru.nemirko.wlcache.bucket.Bucket;
import ru.nemirko.wlcache.bucket.MemoryBucket;

import java.io.Serializable;
import java.util.ArrayDeque;

public class LRU<K,V extends Serializable> extends AbstractAlgorithm<K,V> {
    protected ArrayDeque<K> queue;

    public LRU(Bucket<K, V> bucket) {
        super(bucket);
        queue = new ArrayDeque<>();
    }

    @Override
    public void evictAll() {
        queue.clear();
        bucket.clear();
        cacheHit = 0;
        cacheMiss = 0;
    }

    @Override
    public Pair<K,V> put(K key, V value) {
        if (queue.contains(key)) {
            return refreshValue(key, value);
        } else {
            if (bucket.isFull()) {
                return kickOffOldestAndAddNewKeyandValue(key, value);
            } else {
                if (queue.add(key)) {
                    bucket.put(key, value);
                }
            }
        }
        return null;
    }

    private Pair<K,V> refreshValue(K key, V value) {
        queue.remove(key);
        queue.add(key);
        return new Pair(key, bucket.put(key, value));
    }

    private Pair<K,V> kickOffOldestAndAddNewKeyandValue(K key, V value) {
        K keyForkick = queue.poll();
        V valForKick = bucket.remove(keyForkick);
        queue.add(key);
        bucket.put(key, value);
        return new Pair<>(keyForkick, valForKick);
    }

    @Override
    public V evict(K key) {
        if (queue.remove(key)) {
            return bucket.remove(key);
        }
        return null;
    }

    @Override
    public V get(K key) {
        if (queue.contains(key)) {
            queue.remove(key);
            V value = bucket.get(key);
            if (value != null) {
                cacheHit++;
                queue.add(key);
            }
            return value;
        }
        cacheMiss++;
        return null;
    }

    @Override
    public boolean isFull() {
        return bucket.isFull();
    }

    public Pair<K,V> evictFirst() {
        K key = queue.getFirst();
        V value = bucket.get(key);
        queue.remove(key);
        bucket.remove(key);
        return new Pair<>(key,value);
    }

    public Pair<K,V> evictLast() {
        K key = queue.getLast();
        V value = bucket.get(key);
        queue.remove(key);
        bucket.remove(key);
        return new Pair<>(key,value);
    }

    @Override
    public boolean containsKey(K key) {
        return (bucket.containsKey(key) && queue.contains(key));
    }

    public static void main(String[] args) {
        Algorithm<String, String> alg = new LRU<>(new MemoryBucket<>(4));
        alg.put("1", "TEST1");
        alg.put("2", "TEST2");
        alg.get("3");
        alg.put("3", "TEST3");
        alg.get("1");
        alg.put("4", "TEST4");
        Pair<String, String> p = alg.put("5", "TEST5");
        System.out.println(alg.cacheHit());
        System.out.println(alg.cacheMiss());
    }
}
