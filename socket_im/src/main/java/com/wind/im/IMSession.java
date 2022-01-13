package com.wind.im;

import com.wind.im.core.SessionType;

/**
 * created by wind on 2020/4/9:6:14 PM
 * IM 当前会话上下文
 */
public class IMSession {
    private String sessionId;
    private SessionType sessionType;

    private boolean blurAvatar;

    public IMSession(String sessionId,SessionType sessionType){
      this(sessionId,sessionType,false);
    }
    public IMSession(String sessionId,SessionType sessionType,boolean blurAvatar){
        this.sessionId=sessionId;
        this.sessionType=sessionType;
        this.blurAvatar=blurAvatar;
    }
    public SessionType getSessionType() {
        return sessionType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isBlurAvatar() {
        return blurAvatar;
    }
}
