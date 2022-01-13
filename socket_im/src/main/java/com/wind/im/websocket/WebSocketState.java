package com.wind.im.websocket;

/**
 * created by wind on 2020/4/14:2:15 PM
 */
public enum WebSocketState {

    OPENED(0),ONMESSAGE(1),CLOSING(2),CLOSED(3);

    private int value;
    WebSocketState(int value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
