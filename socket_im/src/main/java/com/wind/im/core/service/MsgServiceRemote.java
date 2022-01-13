package com.wind.im.core.service;

import com.wind.im.core.CommandMessage;
import com.wind.im.core.IMessage;
import com.wind.im.core.LoginMessage;
import com.wind.im.core.request.SendCommandRequest;
import com.wind.im.core.request.SendLoginRequest;
import com.wind.im.core.request.SendMessageRequest;
import com.wind.im.invocation.InvocationFeture;

/**
 * Created By wind
 * on 2020/4/12
 */
public class MsgServiceRemote extends ServiceRemoteImpl implements MsgService {

    @Override
    public InvocationFeture<Void> sendMessage(IMessage msg, boolean resend) {
        SendMessageRequest request=new SendMessageRequest(msg,resend);
        request.setAttachment(transactionCache());
        RequestDispatcher.getInstance().dispatch(request);
        return null;
    }

    @Override
    public InvocationFeture<Void> sendCommandMessage(CommandMessage commandMessage) {
        SendCommandRequest request=new SendCommandRequest(commandMessage);
        request.setAttachment(transactionCache());
        RequestDispatcher.getInstance().dispatch(request);
        return null;
    }

    @Override
    public InvocationFeture<Void> sendLoginMessage(LoginMessage loginMessage) {
        SendLoginRequest request=new SendLoginRequest(loginMessage);
        request.setAttachment(transactionCache());
        RequestDispatcher.getInstance().dispatch(request);
        return null;
    }


}
