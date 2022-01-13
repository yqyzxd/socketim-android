package com.wind.im.websocket;

/**
 * created by wind on 2020/4/9:4:01 PM
 */
public class Response {
    private int status;
    private String statusMessage;

    public Response(int status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
