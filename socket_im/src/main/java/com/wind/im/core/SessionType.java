package com.wind.im.core;

import androidx.annotation.NonNull;

/**
 * 会话类型
 */
public enum SessionType {

    P2P(0),
    ROOM(1),
    TEAM(2);

    private int value;
    SessionType(int value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value+"";
    }


    public int getValue() {
        return value;
    }

    public static SessionType typeOfValue(int value) {
        SessionType[] types;
        int length = (types = values()).length;

        for(int i = 0; i < length; ++i) {
            SessionType type;
            if ((type = types[i]).getValue() == value) {
                return type;
            }
        }

        return P2P;
    }
}
