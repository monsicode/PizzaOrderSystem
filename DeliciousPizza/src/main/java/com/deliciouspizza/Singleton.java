package com.deliciouspizza;

import java.util.HashMap;
import java.util.Map;

public class Singleton<T> {
    private static final Map<Class<?>, Object> instances = new HashMap<>();

    private Singleton() {
        // Приватен конструктор за предотвратяване на директно създаване на обекти
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        synchronized (instances) {
            if (!instances.containsKey(clazz)) {
                try {
                    // Създава нова инстанция, ако няма вече такава
                    instances.put(clazz, clazz.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Не може да се създаде инстанция на " + clazz.getName());
                }
            }
            return (T) instances.get(clazz);
        }
    }
}
