package com.wind.im.core.service;

import com.wind.im.core.IMessage;
import com.wind.im.core.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * created by wind on 2020/4/16:11:23 AM
 */
public class MsgServiceDispatcher {
    protected List<Observer<List<IMessage>>> observers=new ArrayList<>();

    public void dispatchReceivedMessages(List<IMessage> messages){
        for (Observer<List<IMessage>> observer:observers) {
            observer.onEvent(messages);
        }
    }
}
