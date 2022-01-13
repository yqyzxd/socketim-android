package com.wind.im.core;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wind.im.CommandType;
import com.wind.im.bean.ServerCommand;
import com.wind.im.proto.SocketBroadcastProto;
import com.wind.im.proto.SocketTalkProto;
import com.wind.im.util.ByteUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * created by wind on 2020/4/14:1:45 PM
 */
public class ResponseParser {

    public static void parseResponse(byte[] bytes){
//有效数据长度
        byte[] temp=new byte[4];
        temp[0]=bytes[0];
        temp[1]=bytes[1];
        temp[2]=bytes[2];
        temp[3]=bytes[3];
        int dataLen= ByteUtil.getInt(temp);
        //消息类型
        temp[0]=bytes[4];
        temp[1]=bytes[5];
        temp[2]=bytes[6];
        temp[3]=bytes[7];
        int type= ByteUtil.getInt(temp);

        //有效数据
        byte data[]=new byte[dataLen];
        for (int i=8;i<bytes.length;i++){
            data[i-8]=bytes[i];
        }

       // Log.e("JWebSocketClientService", "接收到的数据有效长度:" + dataLen+"消息类型为："+type);

        CommandType commandType=CommandType.typeOfValue(type);
        if (commandType==null){
            return;
        }
        switch (commandType){
            case LOGIN:

                break;
            case TALK:
                parseTalk(data);
                break;
            case BROADCAST:
                parseBroadcast(data);

                break;
            case REPLY:
                //ToastUtil.showToast((Activity) context,"接收到reply消息");
                break;


        }

    }


    private static void parseBroadcast(byte[] data) {
        try {
            SocketBroadcastProto.SocketBroadcast socketBroadcast=SocketBroadcastProto.SocketBroadcast.parseFrom(data);
            String body=socketBroadcast.getBody().toStringUtf8();
            int value=socketBroadcast.getType();
            // int value=socketBroadcastType.getNumber();
            ServerCommand command=new ServerCommand(value,body);
            Observers.getInstance().dispatchServerBroadcast(command);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void parseTalk(byte[] data) {
        List<IMessage> messages=new ArrayList<>();
        try {
            SocketTalkProto.SocketTalk socketTalk=SocketTalkProto.SocketTalk.parseFrom(data);
            String body= socketTalk.getBody().toStringUtf8();
            Log.e("JWebSocketClientService", "body:"+body);
            //Toast.makeText(context,"收到消息："+body,Toast.LENGTH_SHORT).show();
            switch (socketTalk.getType()){
                case TEXT:
                    Message textMessage=createTextMsgFromSocketTalk(socketTalk);
                    messages.add(textMessage);
                    break;
            }
            Observers.getInstance().dispatchMessages(messages);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static Message createTextMsgFromSocketTalk(SocketTalkProto.SocketTalk socketTalk) {

        Message msg=new Message();
        msg.setFrom(String.valueOf(socketTalk.getFrom()));
        msg.setTo(String.valueOf(socketTalk.getTo()));
        String bodyJson=socketTalk.getBody().toStringUtf8();
        String textBody=getTextBody(bodyJson);
        msg.setContent(textBody);

        msg.setUuid(socketTalk.getUUID());

        msg.setStatus(MsgStatus.SUCCESS.getValue());
        int sessionTypeValue=SessionType.P2P.getValue();
        switch (socketTalk.getSessionType()){
            case P2P:
                sessionTypeValue=SessionType.P2P.getValue();
                break;
            case ROOM:
                sessionTypeValue=  SessionType.ROOM.getValue();
                break;
            case GROUP:
                sessionTypeValue=  SessionType.TEAM.getValue();
                break;
        }
        msg.setSessionType(sessionTypeValue);
        msg.setSessionId(msg.getTo());
        msg.setDirect(MsgDirection.In.getValue());
       /* switch (socketTalk.getDirection()){
            case 0:
                msg.setDirect(MsgDirection.Out.getValue());
                msg.setSessionId(msg.getTo());
                break;
            case 1:
                msg.setSessionId(msg.getFrom());
                msg.setDirect(MsgDirection.In.getValue());
                break;
        }*/

        msg.setTimestamp(socketTalk.getTimestamp());

        //json转map
        Map<String,Object> extension=getExt(socketTalk.getExt().toStringUtf8());
        msg.setRemoteExt(extension);
        return msg;
    }

    private static Map<String, Object> getExt(String extJson) {
        Map<String, Object> map= new HashMap<>();
        try {

            JSONObject jsonObject=new JSONObject(extJson);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()){
                String key=keys.next();
                Object value=jsonObject.opt(key);
                map.put(key,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static String getTextBody(String bodyJson) {

        try {
            JSONObject jsonObject = new JSONObject(bodyJson);
            String content=jsonObject.getString("msg");
            return content;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}
