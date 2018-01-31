package ru.nemirko.wlcache.bucket;

import java.util.HashMap;

public class MemoryBucket<K,V> extends AbstractBucket<K,V> {
    private HashMap<K, V> map;

    public MemoryBucket(int maxSize) {
        super(maxSize);
        map = new HashMap<>(maxSize);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        if (!isFull()) {
            return map.put(key, value);
        }
        return null;
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    public static void main(String[] args) {
        Bucket<String, String> bucket = new MemoryBucket<>(4);
        assert bucket.isEmpty() == true;
        assert bucket.isFull() == false;
        String s = bucket.put("1", "TEST1");
        assert s == null;
        assert bucket.size() == 1;
        s = bucket.put("2", "TEST2");
        assert s == null;
        s = bucket.put("3", "TEST3");
        assert s == null;
        assert bucket.size() == 3;
        s = bucket.put("2", "TEST2");
        assert s.equals("TEST2");
        assert bucket.size() == 3;
        assert bucket.get("2").equals("TEST2");
        s = bucket.remove("2");
        assert s.equals("TEST2");
        assert bucket.size() == 2;
        assert bucket.get("1").equals("TEST1");
        assert bucket.isFull() == false;
        s = bucket.put("2", "TEST2");
        assert s == null;
        s = bucket.put("4", "TEST4");
        assert s == null;
        assert bucket.isEmpty() == false;
        assert bucket.isFull() == true;
        bucket.clear();
        assert bucket.isEmpty() == false;
        System.out.println("OK");
    }
}
