//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;

public class PasswordSecretUtil {
    public PasswordSecretUtil() {}

    public static String createSalt() {
        byte[] salt = new byte[16];

        try {
            SecureRandom randSecure = SecureRandom.getInstance("SHA1PRNG");
            randSecure.nextBytes(salt);
            return Base64.encodeBase64String(salt);
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
            return "";
        }
    }
}
