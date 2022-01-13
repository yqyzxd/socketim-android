package com.wind.im.core;

/**
 * created by wind on 2020/4/13:11:35 AM
 */
public class CommandMessage {
    private String sessionId;//同to
    private SessionType sessionType;

    /**
     * command类型
     */
    private int type;
    /**
     * command携带的数据
     */
    private String data;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
