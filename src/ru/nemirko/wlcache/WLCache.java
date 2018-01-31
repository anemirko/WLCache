package ru.nemirko.wlcache;

import javafx.util.Pair;
import ru.nemirko.wlcache.algorithm.Algorithm;
import ru.nemirko.wlcache.algorithm.LRU;
import ru.nemirko.wlcache.algorithm.MLRU;
import ru.nemirko.wlcache.bucket.FileBucket;
import ru.nemirko.wlcache.bucket.MemoryBucket;

import java.io.Serializable;

import static ru.nemirko.wlcache.Algorithm.LRU;

public class WLCache<K, V extends Serializable> {

    private ru.nemirko.wlcache.algorithm.Algorithm<K,V> l1Cache;
    private Algorithm<K,V> l2Cache;
    private int l1Size;
    private int l2Size;
    private String root;
    private float proportion = .5f;

    public WLCache(ru.nemirko.wlcache.Algorithm algLevel1, int l1Size, ru.nemirko.wlcache.Algorithm alglevel2, int l2Size, String root) {
        this.l1Size = l1Size;
        this.l2Size = l2Size;
        this.root = root;
        if (algLevel1 == LRU) {
            l1Cache = new LRU<>(new MemoryBucket<>(l1Size));
        } else {
            int size1 = (int) (l1Size * proportion);
            int size2 = l1Size - size1;
            l1Cache = new MLRU<>(new MemoryBucket<>(size1), new MemoryBucket<>(size2));
        }
        if (alglevel2 == LRU) {
            l2Cache = new LRU<>(new FileBucket<>(root, l2Size));
        } else {
            l2Cache = new LRU<>(new FileBucket<>(root, l2Size));
        }
    }

    public Pair<K,V> put(K key, V value) {
        if (l1Cache.containsKey(key)) {
            return l1Cache.put(key, value);
        } else {
            if (l2Cache.containsKey(key)) {
                return l2Cache.put(key, value);
            } else {
                Pair<K,V> kickedFromL1 = l1Cache.put(key, value);
                if (kickedFromL1 != null) {
                    return l2Cache.put(kickedFromL1.getKey(), kickedFromL1.getValue());
                }
            }
        }
        return null;
    }

    private V get(K key) {
        V value = l1Cache.get(key);
        if (value == null) {
            value = l2Cache.get(key);
        }
        return value;
    }

    public static void main(String[] args) {
        //WLCache<String, String> cache = new WLCache<>(ru.nemirko.wlcache.Algorithm.MLRU, 6, LRU,6, "c://123");
        WLCache<String, String> cache = new WLCache<>(ru.nemirko.wlcache.Algorithm.MLRU, 6, LRU,6, "/Users/anemirko/Documents/");
        cache.put("1", "TEST1");
        cache.put("2", "TEST2");
        cache.put("3", "TEST3");
        cache.get("1");
        cache.put("4", "TEST4");
        cache.put("5", "TEST5");
        cache.get("4");
        cache.put("6", "TEST6");
        cache.put("7", "TEST7");
        cache.put("8", "TEST8");
        cache.put("9", "TEST9");
        cache.get("44");
        cache.put("10", "TEST10");
        cache.put("11", "TEST11");
        cache.get("8");
        cache.get("1");
        cache.put("12", "TEST12");
        cache.put("13", "TEST13");
        System.out.println(String.format("Level1(miss=%d, hit=%d)\tLevel2(miss=%d, hit=%d)", cache.l1Cache.cacheMiss(),
                cache.l1Cache.cacheHit(),cache.l2Cache.cacheMiss(),cache.l2Cache.cacheHit()));
    }
}
