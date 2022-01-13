package com.wind.im.core.request;

import com.wind.im.CommandType;
import com.wind.im.core.service.Transaction;

import java.io.Serializable;

/**
 * Created By wind
 * on 2020/4/12
 */
public abstract class Request implements Serializable {
    private Transaction transaction;
    private CommandType commandType;
    public Request(CommandType commandType){
        this.commandType=commandType;
    }
    public void setAttachment(Transaction transaction) {
        this.transaction=transaction;
    }
    public Transaction getTransaction() {
        return transaction;
    }

    public CommandType getCommandType() {
        return commandType;
    }
}
