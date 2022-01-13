package com.wind.im.core.service;

import com.google.protobuf.ByteString;
import com.wind.im.SocketCommand;
import com.wind.im.core.CommandMessage;
import com.wind.im.core.IMessage;
import com.wind.im.core.LoginMessage;
import com.wind.im.core.request.Request;
import com.wind.im.core.request.SendCommandRequest;
import com.wind.im.core.request.SendLoginRequest;
import com.wind.im.core.request.SendMessageRequest;
import com.wind.im.proto.SocketLoginProto;
import com.wind.im.proto.SocketReplyProto;
import com.wind.im.proto.SocketTalkProto;

/**
 * Created By wind
 * on 2020/4/12
 */
public class PacketData {
    private byte[]data;
    public PacketData(Request request) {
        if (request instanceof SendMessageRequest){
            data=handleMessageRequest((SendMessageRequest) request);
        }else if (request instanceof SendCommandRequest){
            data=handleCommandRequest((SendCommandRequest) request);
        }else if (request instanceof SendLoginRequest){
            data=handleLoginRequest((SendLoginRequest) request);
        }
    }

    private byte[] handleLoginRequest(SendLoginRequest request) {
        LoginMessage loginMessage=request.getLoginMessage();
        SocketLoginProto.SocketLogin socketLogin =
                SocketLoginProto
                        .SocketLogin
                        .newBuilder()
                        .setToken(loginMessage.getToken())
                        .setNetwork(loginMessage.getNetwork())
                        .setOs(loginMessage.getOs())
                        .build();

        return SocketCommand.ofCmd(request.getCommandType().getValue(),socketLogin.toByteArray()).getCommandBytes();
    }

    private byte[] handleCommandRequest(SendCommandRequest request) {
        CommandMessage commandMessage=request.getCommandMessage();

        SocketReplyProto.SocketReply socketReply = SocketReplyProto.SocketReply.newBuilder()
                .setType(commandMessage.getType())
                .setBody(ByteString.copyFromUtf8(commandMessage.getData()))
                .build();
        return SocketCommand.ofCmd(request.getCommandType().getValue(),socketReply.toByteArray()).getCommandBytes();
    }

    private byte[] handleMessageRequest(SendMessageRequest request) {
        IMessage message=request.getMessage();
        SocketTalkProto.SocketTalk.TalkMsgType value=null;
        String content="";
        switch (message.getMsgType()){
            case TEXT:
                value=SocketTalkProto.SocketTalk.TalkMsgType.TEXT;
                StringBuilder sBuilder=new StringBuilder();
                sBuilder.append("{")
                        .append("\"")
                        .append("msg")
                        .append("\"")
                        .append(":")
                        .append("\"")
                        .append(message.getContent())
                        .append("\"")
                        .append("}");
                content=sBuilder.toString();
                break;
        }


        SocketTalkProto.SocketTalk socketTalk = SocketTalkProto.SocketTalk.newBuilder()
                .setFrom(Long.parseLong(message.getFrom()))
                .setTo(Long.parseLong(message.getSessionId()))
                .setType(value)
                .setBody(ByteString.copyFromUtf8(content))
                .setUUID(message.getUuid())
                .setTimestamp(message.getTimestamp() / 1000)
                .setSessionType(SocketTalkProto.SocketTalk.TalkSessionType.ROOM)
                .setOption(ByteString.EMPTY)
                .setPushcontent(message.getPushContent())
                .setExt(ByteString.copyFromUtf8("{}"))
                .setDirection(message.getDirect().getValue())
                .setMsgAck(0)
                .build();
       return SocketCommand
                .ofCmd(request.getCommandType().getValue(),
                        socketTalk.toByteArray()).getCommandBytes();
    }

    public byte[] getData() {
        return data;
    }
}
