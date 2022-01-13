package com.wind.im.core.service;

import java.util.List;

/**
 * Created By wind
 * on 2020/4/12
 */
public class SendTaskManager {
    private List<RequestDispatcher.SendTask> pendings;
    public boolean pend(RequestDispatcher.SendTask task) {
        return pendings.add(task);
    }
}
