package com.wind.im.bean;

public class ServerCommand {

    private int type;
    private String body;

    public ServerCommand(int type, String body) {
        this.type = type;
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
