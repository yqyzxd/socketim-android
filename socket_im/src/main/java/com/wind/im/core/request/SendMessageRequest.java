package com.wind.im.core.request;

import com.wind.im.CommandType;
import com.wind.im.core.IMessage;

/**
 * Created By wind
 * on 2020/4/12
 */
public class SendMessageRequest extends Request{
    private IMessage message;
    private boolean resend;

    public SendMessageRequest(IMessage message, boolean resend) {
        super(CommandType.TALK);
        this.message = message;
        this.resend = resend;
    }

    public IMessage getMessage() {
        return message;
    }


    public boolean isResend() {
        return resend;
    }



}
