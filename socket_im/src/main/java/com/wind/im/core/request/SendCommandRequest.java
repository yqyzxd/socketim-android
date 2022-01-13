package com.wind.im.core.request;

import com.wind.im.CommandType;
import com.wind.im.core.CommandMessage;

/**
 * created by wind on 2020/4/13:11:19 AM
 *
 * 客户端发送透传消息
 */
public class SendCommandRequest extends Request {
    private CommandMessage commandMessage;
    public SendCommandRequest(CommandMessage commandMessage) {
        super(CommandType.REPLY);
        this.commandMessage=commandMessage;
    }

    public CommandMessage getCommandMessage() {
        return commandMessage;
    }
}
