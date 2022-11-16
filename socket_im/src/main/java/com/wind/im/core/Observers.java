package com.wind.im.core;

import com.wind.im.bean.ServerCommand;

import java.util.ArrayList;
import java.util.List;

public class Observers {

    private List<Observer<List<IMessage>>> mMessageObservers = new ArrayList<>();
    private List<Observer<Void>> mOpenObservers = new ArrayList<>();
    private List<Observer<ServerCommand>> mServerBroadcastObservers = new ArrayList<>();
    private static Observers sInstance;

    public static Observers getInstance() {
        if (sInstance == null) {
            synchronized (Observers.class) {
                if (sInstance == null) {
                    sInstance = new Observers();
                }
            }
        }
        return sInstance;
    }

    public void observeReceiveMessages(Observer<List<IMessage>> observer, boolean register) {
        if (register)
            mMessageObservers.add(observer);
        else
            mMessageObservers.remove(observer);
    }

    public void observeSocketOpen(Observer<Void> observer, boolean register) {
        if (register)
            mOpenObservers.add(observer);
        else
            mOpenObservers.remove(observer);
    }

    public void observeServerBroadcast(Observer<ServerCommand> observer
            , boolean register) {
        if (register)
            mServerBroadcastObservers.add(observer);
        else
            mServerBroadcastObservers.remove(observer);
    }

    /**
     * 派发聊天消息
     *
     * @param messages
     */
    public void dispatchMessages(List<IMessage> messages) {
        try {
            for (Observer<List<IMessage>> observer : mMessageObservers) {
                observer.onEvent(messages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 派发socket连接成功消息
     */
    public void dispatchOpened() {
        try {
            for (Observer<Void> observer : mOpenObservers) {
                observer.onEvent(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 派发服务端的透传
     */
    public void dispatchServerBroadcast(ServerCommand serverCommand) {
        try {
            for (Observer<ServerCommand> observer : mServerBroadcastObservers) {
                observer.onEvent(serverCommand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
