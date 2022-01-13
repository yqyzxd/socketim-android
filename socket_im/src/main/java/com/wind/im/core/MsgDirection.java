package com.wind.im.core;

import androidx.annotation.NonNull;

public enum  MsgDirection {
    Out(0),
    In(1);

    private int value;
    MsgDirection(int value) {
        this.value = value;
    }

    public final int getValue() {
        return this.value;
    }

    public static MsgDirection directionOfValue(int value) {
        MsgDirection[] directions;
        int length = (directions = values()).length;

        for(int i = 0; i < length; ++i) {
            MsgDirection direction;
            if ((direction = directions[i]).getValue() == value) {
                return direction;
            }
        }

        return Out;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
