package com.wind.im.core;

import com.wind.im.util.UUIDs;

/**
 * created by wind on 2020/4/9:4:59 PM
 */
public class MessageBuilder {

    /**
     * 创建文件消息
     * @param sessionId
     * @param sessionType
     * @param content
     * @return
     */
    public static IMessage createTextMessage(String sessionId, SessionType sessionType, String content) {
        Message message= initSendMessage(sessionId, sessionType);
        message.setMsgType(MsgType.TEXT);
        message.setContent(content);
        return message;
    }
   private static Message initSendMessage(String sessionId, SessionType sessionType) {
        Message message =new Message();
        message.setUuid(UUIDs.randomUUID());
        message.setSessionId(sessionId);
        message.setTo(sessionId);
       //todo 从登陆用户中获取登录这账号
       // message.setFrom(c.k());
        message.setDirect(MsgDirection.Out.getValue());
        message.setStatus(MsgStatus.SENDING.getValue());
        message.setSessionType(sessionType.getValue());
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }
}
