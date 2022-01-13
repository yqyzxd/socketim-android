package com.wind.im;

import com.wind.im.core.MsgStatus;

public enum CommandType {

    LOGIN(1),
    TALK(2),
    BROADCAST(3),
    REPLY(4);


    private int value;
    CommandType(int value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }

    public static CommandType typeOfValue(int value) {
        CommandType[] commandTypes;
        int len = (commandTypes = values()).length;

        for(int i = 0; i < len; ++i) {
            CommandType type;
            if ((type = commandTypes[i]).getValue() == value) {
                return type;
            }
        }

        return null;
    }
}
