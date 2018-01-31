package ru.nemirko.wlcache.bucket;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileBucket<K,V extends Serializable> extends AbstractBucket<K,V> {
    private Path root;
    private HashMap<K, Path> map;

    public FileBucket(String root, int maxSize) {
        super(maxSize);
        this.root = Paths.get(root);
        if (Files.isDirectory(this.root)) {
            map = new HashMap<>(maxSize);
        } else {
            throw new IllegalArgumentException("You must specify the correct path to the file cache");
        }
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
        map.values().forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        map.clear();
    }

    @Override
    public V get(K key) {
        if (map.containsKey(key)) {
            return getObjectFromFile(map.get(key));
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (!isFull()) {
            Path previous = map.put(key, saveObjectToFile(key, value));
            if (previous != null) {
                return getObjectFromFile(previous);
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        Path file = map.remove(key);
        if (file != null) {
            V removed = getObjectFromFile(file);
            try {
                Files.delete(file);
                return removed;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    private V getObjectFromFile(Path file) {
        try {
            byte[] data = Files.readAllBytes(file);
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                return (V) ois.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Path saveObjectToFile(K key, V object) {
        Path file = Paths.get(root.toString(), key.toString());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(object);
            return file;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        Bucket<String, String> bucket = new FileBucket<>("c://Logs", 4);
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
        bucket.clear();
        assert bucket.isEmpty() == false;
        assert bucket.isFull() == true;
        System.out.println("OK");
    }
}
