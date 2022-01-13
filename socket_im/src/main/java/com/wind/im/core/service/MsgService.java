package com.wind.im.core.service;

import com.wind.im.core.CommandMessage;
import com.wind.im.core.IMessage;
import com.wind.im.core.LoginMessage;
import com.wind.im.invocation.InvocationFeture;

/**
 * Created By wind
 * on 2020/4/12
 */
public interface MsgService {
    /**
     * 发送普通消息
     * @param msg
     * @param resend
     * @return
     */
    InvocationFeture<Void> sendMessage(IMessage msg, boolean resend);

    /**
     * 发送透传消息
     * @param commandMessage
     * @return
     */
    InvocationFeture<Void> sendCommandMessage(CommandMessage commandMessage);

    InvocationFeture<Void> sendLoginMessage(LoginMessage loginMessage);
}
