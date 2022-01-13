package com.wind.im.core;

import com.wind.im.core.attachment.MsgAttachment;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息 接口
 */
public interface IMessage<T> extends Serializable {
    /**消息唯一标识*/
    String getUuid();
    /**是否是同一条消息*/
    //boolean isTheSame(IMessage message);
    /**会话id 单聊时为对方账号*/
    String getSessionId();
    void setSessionId(String sessionId);
    /**会话类型*/
    SessionType getSessionType();
    void setSessionType(int sessionTypeValue);

    void setFrom(String fromAccount);
    String getFrom();

    /**消息类型*/
    MsgType getMsgType();
    void setMsgType(MsgType msgType);
    /**消息状态*/
    MsgStatus getStatus();
    void setStatus(int status);
    //void setStatus(MsgStatus status);
    /**发送/接收*/
    MsgDirection getDirect();
    void setDirect(int direction);

    long getTimestamp();
    void setTimestamp(long timestamp);

    Map<String, Object> getRemoteExt();

    void setRemoteExt(Map<String, Object> extension);

    Map<String, Object> getLocalExt();
    void setLocalExt(Map<String, Object> extension);

    String getPushContent();
    void setPushContent(String pushContent);


    /**对方是否已读*/
    boolean isRemoteRead();
    void setRemoteReaded();
    boolean needMsgAck();

    void setMsgAck();

    boolean hasSendAck();

    MsgAttachment getAttachment();

    boolean isTheSame(IMessage message);

    String getContent();

}
