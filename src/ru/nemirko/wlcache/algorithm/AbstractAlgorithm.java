package ru.nemirko.wlcache.algorithm;

import ru.nemirko.wlcache.bucket.Bucket;

import java.io.Serializable;

abstract class AbstractAlgorithm<K,V extends Serializable> implements Algorithm<K,V>{
    protected int cacheHit;
    protected int cacheMiss;
    protected Bucket<K,V> bucket;

    public AbstractAlgorithm() {
    }

    public AbstractAlgorithm(Bucket<K, V> bucket) {
        this.bucket = bucket;
    }

    public int cacheHit() {
        return cacheHit;
    }

    public int cacheMiss() {
        return cacheMiss;
    }
}
