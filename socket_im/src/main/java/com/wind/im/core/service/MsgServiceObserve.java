package com.wind.im.core.service;

import com.wind.im.core.IMessage;
import com.wind.im.core.Observer;

import java.util.List;

/**
 * created by wind on 2020/4/13:9:44 AM
 * 消息服务观察者
 */
public interface MsgServiceObserve {
    void observeReceiveMessage(Observer<List<IMessage>> observer, boolean register);
}
