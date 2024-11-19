package com.deliciouspizza.utils;

import org.mindrot.jbcrypt.BCrypt;

public class AdminUtils {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

}
//admin123
