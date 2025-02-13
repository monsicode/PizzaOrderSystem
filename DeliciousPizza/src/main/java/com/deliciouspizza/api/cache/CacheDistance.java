package com.deliciouspizza.api.cache;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CacheDistance<K, V> {

    private final String filePath;
    private final Gson gson;
    private Map<K, V> cache;

    private static final int HOURS_TILL_UPDATE = 8;

    public CacheDistance(String filePath, Class<K> keyClass, Class<V> valueClass) {
        this.filePath = filePath;
        this.gson = new Gson();
        this.cache = new HashMap<>();
        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();

//        if (isCacheExpired(Duration.ofHours(HOURS_TILL_UPDATE))) {
//            clearCache();
//        } else {
//            loadCacheFromFile(type);
//        }

        loadCacheFromFile(type);
    }

    private void loadCacheFromFile(Type type) {
        try (FileReader reader = new FileReader(filePath)) {
            cache = gson.fromJson(reader, type);
            if (cache == null) {
                cache = new HashMap<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            cache = new HashMap<>();
        }
    }

    public void saveCacheToFile() {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            System.err.println("Error saving cache to file: " + e.getMessage());
        }
    }

    public void put(K key, V value) {
        cache.put(key, value);
        saveCacheToFile();
    }

    public V get(K key) {
        return cache.get(key);
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    private boolean isCacheExpired(Duration validityDuration) {
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }
        long lastModified = file.lastModified();
        Instant lastModifiedInstant = Instant.ofEpochMilli(lastModified);
        return Instant.now().isAfter(lastModifiedInstant.plus(validityDuration));
    }

    private void clearCache() {
        cache.clear();
        saveCacheToFile();
    }

}