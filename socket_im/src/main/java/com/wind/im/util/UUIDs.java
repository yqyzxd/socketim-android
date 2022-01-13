package com.wind.im.util;

import java.util.UUID;

public final class UUIDs {
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /*public static String a(String var0) {
        int var1;
        if ((var1 = var0.lastIndexOf(46)) == -1) {
            return "";
        } else {
            for(int var2 = var1 + 1; var2 < var0.length(); ++var2) {
                char var3;
                if (((var3 = var0.charAt(var2)) < 'a' || var3 > 'z') && (var3 < 'A' || var3 > 'Z') && (var3 < '0' || var3 > '9')) {
                    return "";
                }
            }

            return var0.substring(var1 + 1, var0.length());
        }
    }*/
}