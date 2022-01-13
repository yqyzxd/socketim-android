package com.wind.im.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.wind.im.websocket.IWebSocketClient;

/**
 * created by wind on 2020/4/13:3:24 PM
 */
public class Rpc {
    public static final int WHAT_CLIENT_MESSENGER=2020;
    public static final int WHAT_BYTES_FROM_CLIENT=1024;


    public static final int WHAT_ACTION_ON_MESSAGE=3000;
    public static final int WHAT_ACTION_ON_OPEN=3001;

    public static final String KEY_BYTES="key_bytes";
    /**
     * 服务端Messenger对象
     */
    private final Messenger mMessenger;
    /**
     * 客户端Messenger对象
     */
    private Messenger mClientMessenger=null;

    private Handler mHandler;
    public Rpc() {
        HandlerThread handlerThread = new HandlerThread("RemoteAgent");
        handlerThread.start();
        this.mMessenger = new Messenger(new MessageHandler(handlerThread.getLooper()));
        mHandler=new Handler();
    }

    public IBinder getBinder() {
        //将Messenger对象的Binder返回给客户端
        return mMessenger.getBinder();
    }

    public void sendSocketOpenedMessage(){
        if (mClientMessenger!=null){
            try {
               // System.out.println("Rpc sendSocketOpenedMessage");
                Message message=Message.obtain(null,WHAT_ACTION_ON_OPEN);
                mClientMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            if(mHandler!=null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendSocketOpenedMessage();
                    }
                }, 300);
            }
        }
    }

    public void sendToClient(byte[] bytes){
       // System.out.println("sendToClient bytes!=null"+(bytes!=null));
        if (mClientMessenger!=null){
            try {
                Message message=Message.obtain(null,WHAT_ACTION_ON_MESSAGE);
                Bundle bundle=new Bundle();
                bundle.putByteArray(Rpc.KEY_BYTES,bytes);
                message.setData(bundle);
                mClientMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private IWebSocketClient mWebSocketClient;
    public void setWebSocketClient(IWebSocketClient webSocketClient) {
        this.mWebSocketClient=webSocketClient;
    }

    /**
     * 处理客户端发来的消息
     */
    private class MessageHandler extends Handler {
        MessageHandler(Looper looper) {
            super(looper);
        }
        public final void handleMessage(Message message) {
            //Log.e("LocalAgent", "接收到客户端发来的消息");
            switch (message.what){
                case WHAT_BYTES_FROM_CLIENT:

                    sendToServer(message);
                    break;
                case WHAT_CLIENT_MESSENGER:
                    //仅仅是为了保存客户端的Messenger
                    mClientMessenger=message.replyTo;
                    //sendSocketOpenedMessage();
                    break;
            }
        }

        private void sendToServer(Message message) {
            try {
                mClientMessenger= message.replyTo;
                Bundle bundle=message.getData();
                if (bundle!=null){
                    byte[] bytes=bundle.getByteArray(KEY_BYTES);
                    //调用websocket 接口发送数据
                    if (mWebSocketClient!=null)
                        mWebSocketClient.send(bytes);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
