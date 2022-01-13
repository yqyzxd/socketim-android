package com.wind.im.core.service;

import com.wind.im.core.IMessage;
import com.wind.im.core.Observer;

import java.util.List;

/**
 * created by wind on 2020/4/13:10:14 AM
 */
public class MsgServiceObserveRemote extends MsgServiceDispatcher implements MsgServiceObserve {

    @Override
    public void observeReceiveMessage(Observer<List<IMessage>> observer, boolean register) {
        if (register){
            observers.add(observer);
        }else {
            observers.remove(observer);
        }
    }


}
