package com.tomclaw.molecus.util;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.utils.L;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by solkin on 16.12.15.
 */
public class Sha256FileNameGenerator implements FileNameGenerator {

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    private static final String HASH_ALGORITHM = "SHA-256";

    public Sha256FileNameGenerator() {
    }

    public String generate(String imageUri) {
        return bytesToHex(getSha256(imageUri.getBytes()));
    }

    private byte[] getSha256(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest e = MessageDigest.getInstance(HASH_ALGORITHM);
            e.update(data);
            hash = e.digest();
        } catch (NoSuchAlgorithmException var4) {
            L.e(var4);
        }

        return hash;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
