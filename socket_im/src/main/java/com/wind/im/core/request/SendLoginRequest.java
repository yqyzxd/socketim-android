package com.wind.im.core.request;

import com.wind.im.CommandType;
import com.wind.im.core.LoginMessage;

/**
 * created by wind on 2020/4/14:2:40 PM
 */
public class SendLoginRequest extends Request {
    private LoginMessage loginMessage;
    public SendLoginRequest(LoginMessage loginMessage) {
        super(CommandType.LOGIN);
        this.loginMessage=loginMessage;
    }

    public LoginMessage getLoginMessage() {
        return loginMessage;
    }
}
