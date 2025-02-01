package com.deliciouspizza.utils;

public class Validators {

    public static <T> void nullCheck(T obj, String errMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errMessage);
        }
    }

//    public static String validateArgumentCount(String[] args, int countNeededArgs, String message) {
//        if (args.length != countNeededArgs) {
//            return message;
//        }
//    }

}
