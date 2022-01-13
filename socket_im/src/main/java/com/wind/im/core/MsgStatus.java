package com.wind.im.core;

public enum  MsgStatus {

    DRAFT(-1),
    SENDING(0),
    SUCCESS(1),
    FAIL(2),
    READ(3),
    UNREAD(4);

    private int value;

    MsgStatus(int var3) {
        this.value = var3;
    }

    public static MsgStatus statusOfValue(int var0) {
        MsgStatus[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            MsgStatus var4;
            if ((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return SENDING;
    }

    public final int getValue() {
        return this.value;
    }
}
