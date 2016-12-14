package com.tanpn.messenger.utils;

import android.util.Base64;

/**
 * Created by phamt_000 on 11/28/16.
 */
public class Encryption {
    private static final String scurityKey = "QKSDFOCLSDPE";
    public static String encrypt(String string){
        String s = string + scurityKey;
        byte[] encodeValue = Base64.encode(s.getBytes(), Base64.DEFAULT);

        return new String(encodeValue);
    }

    public static String decrypt(String string){

        byte[] b = string.getBytes();
        byte[] decodeValue = Base64.decode(b, Base64.DEFAULT);

        String s = new String(decodeValue);

        return s.replaceAll(scurityKey, "");
    }
}
