package com.wind.im.core;

import com.wind.im.core.attachment.MsgAttachment;

import java.util.HashMap;
import java.util.Map;

public class Message implements IMessage {

    private String uuid;
    private String from;
    private String to;
    private String sessionId;
    private SessionType sessionType=SessionType.P2P;
    private MsgStatus msgStatus;
    private MsgDirection msgDirection;
    private long timestamp;
    private Map<String, Object> ext=new HashMap<>();
    /**是否需要消息回执*/
    private boolean msgAck;
    /**是否已经发送了消息回执*/
    private boolean hasSendAck;
    /**对方是否已读*/
    private boolean remoteReaded;
    /**消息类型 TEXT IMAGE VIDEO*/
    private MsgType msgType= MsgType.TEXT;
    /**文本消息内容*/
    private String  content;
    private String pushContent;
    private MsgAttachment attachment;
    public Message(){
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public SessionType getSessionType() {
        return sessionType;
    }
    @Override
    public void setSessionType(int sessionTypeValue) {
        this.sessionType = SessionType.typeOfValue(sessionTypeValue);
    }

    @Override
    public void setFrom(String fromAccount) {
        this.from=fromAccount;
    }

    @Override
    public String getFrom() {
        return from;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public String getTo() {
        return to;
    }

    @Override
    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    @Override
    public MsgStatus getStatus() {
        return msgStatus;
    }

    public void setStatus(int status) {
        this.msgStatus=MsgStatus.statusOfValue(status);
    }

    @Override
    public MsgDirection getDirect() {
        return msgDirection;
    }
    public void setDirect(int direction) {
        this.msgDirection=MsgDirection.directionOfValue(direction);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Map<String, Object> getRemoteExt() {
        return ext;
    }

    @Override
    public Map<String, Object> getLocalExt() {
        return null;
    }

    @Override
    public void setLocalExt(Map extension) {

    }

    @Override
    public void setRemoteExt(Map extension) {
        this.ext=extension;
    }

    public long getTimestamp() {
        return timestamp;
    }




    @Override
    public String getPushContent() {
        return pushContent==null?"":pushContent;
    }

    @Override
    public void setPushContent(String pushContent) {
        this.pushContent=pushContent;
    }

    @Override
    public boolean isRemoteRead() {
        return remoteReaded;
    }

    @Override
    public void setRemoteReaded() {
        remoteReaded=true;
    }

    @Override
    public boolean needMsgAck() {
        return msgAck;
    }

    @Override
    public void setMsgAck() {
        this.msgAck=true;
    }

    public void setHasSendAck(boolean hasSendAck) {
        this.hasSendAck = hasSendAck;
    }
    @Override
    public boolean hasSendAck() {
        return hasSendAck;
    }

    @Override
    public MsgAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(MsgAttachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean isTheSame(IMessage message) {
        if (message==null){
            return false;
        }
        return message.getUuid().equals(getUuid());
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String msg){
      this.content=msg;
    }

    public static class TextBody{
        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }


}
