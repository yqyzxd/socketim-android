package com.wind.im.core;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageParser {


    public IMessage parseToMessage(String jsonMessage){
        IMessage message=null;
        try {
            JSONObject jsonObjectMessage=new JSONObject(jsonMessage);
            int type=jsonObjectMessage.getInt("type");

            MsgType msgType= MsgType.typeOfValue(type);
            switch (msgType){
                case TEXT:

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }


}
