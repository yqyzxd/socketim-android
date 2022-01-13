package com.wind.im.core;

import androidx.annotation.NonNull;

/**
 * 消息类型
 */
public enum  MsgType {
    /**文本消息*/
    TEXT(0, "");


    private final int value;
    final String desc;
    MsgType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static MsgType typeOfValue(int value) {
        MsgType[] types;
        int length = (types = values()).length;

        for(int i = 0; i < length; ++i) {
            MsgType type;
            if ((type = types[i]).getValue() == value) {
                return type;
            }
        }

        return TEXT;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
