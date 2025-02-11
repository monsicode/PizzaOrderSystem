package com.deliciouspizza.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Singleton<T> {
    private static final Map<Class<?>, Object> INSTANCES = new ConcurrentHashMap<>();

    private Singleton() {
        // Private constructor to prevent direct object creation
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        synchronized (INSTANCES) {
            if (!INSTANCES.containsKey(clazz)) {
                try {
                    INSTANCES.put(clazz, clazz.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Cannot create an instance of " + clazz.getName());
                }
            }
            return (T) INSTANCES.get(clazz);
        }
    }
}

//@SuppressWarnings("unchecked")
//public static <T> T getInstance(Class<T> clazz) {
//    synchronized (INSTANCES) {
//        return (T) INSTANCES.computeIfAbsent(clazz, key -> {
//            try {
//                return clazz.getDeclaredConstructor().newInstance();
//            } catch (Exception e) {
//                throw new IllegalStateException("Cannot create instance of " + clazz.getName(), e);
//            }
//        });
//    }
//}