package com.wind.im.core.service;

import com.wind.im.core.request.Request;

/**
 * Created By wind
 * on 2020/4/12
 */
public class RequestDispatcher {

    private SendTaskManager sendTaskManager = new SendTaskManager();
    private LocalAgent agent;
    private static RequestDispatcher sInstance = null;
    private RequestDispatcher(){
        agent=LocalAgent.getInstance();
    }
    public static RequestDispatcher getInstance() {
        if (sInstance == null) {
            synchronized (RequestDispatcher.class) {
                if (sInstance == null) {
                    sInstance = new RequestDispatcher();
                }
            }
        }
        return sInstance;
    }

    public boolean dispatch(Request request) {
        SendTask task = new SendRequestTask(request);
        return addSendTask(task);
    }

    public boolean addSendTask(SendTask task) {
        if (task == null) {
            return false;
        }
        boolean canSend = true;//false;
        Request request = task.getRequest();

        //todo 检查登录状态

        boolean accept = canSend;

       /* if (task.getTimeout() > 0) {
            accept = sendTaskManager.pend(task);
        }*/

        if (canSend) {
            agent.send(new PacketData(request));
        }

        return accept;
    }


    public interface SendTask {
        Request getRequest();
    }

    public static class SendRequestTask implements SendTask {
        private Request request;

        public SendRequestTask(Request request) {
            this.request = request;
        }
        public Request getRequest() {
            return request;
        }
    }
}
