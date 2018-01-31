package ru.nemirko.wlcache.bucket;

abstract class AbstractBucket<K,V> implements Bucket<K,V> {
    private int maxSize;

    public AbstractBucket(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("\n" +
                    "You must specify the correct maxsize for bucket");
        }
        this.maxSize = maxSize;
    }

    public int maxSize() {
        return maxSize;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return size() == maxSize;
    }

}
