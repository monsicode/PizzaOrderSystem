package com.deliciouspizza.utils;

public class Utils {

    public static <T> void nullCheck(T obj, String errMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errMessage);
        }
    }

}
