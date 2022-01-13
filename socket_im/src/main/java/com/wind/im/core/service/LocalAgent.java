package com.wind.im.core.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;


import com.wind.im.core.Observers;
import com.wind.im.core.ResponseParser;
import com.wind.im.invocation.Handlers;
import com.wind.im.service.Rpc;
import com.wind.im.service.WebSocketClientService;
import com.wind.mlog.MLog;

import java.util.List;

/**
 * Created By wind
 * on 2020/4/12
 */
public class LocalAgent {
    public static final String TAG=LocalAgent.class.getSimpleName();
    private Context context;
    private List<RequestDispatcher.SendTask> pendings;
    private Messenger replyTo;
    private Messenger serverMessenger;

    private boolean mBinded;
    private LocalAgent(Context context){
        //todo 检查是否是main 线程
        this.context=context;
        //Log.e(TAG, "LocalAgent created");
        /*pendings=new ArrayList<>();*/
        HandlerThread coreThread=new HandlerThread(TAG);
        coreThread.start();
        replyTo=new Messenger(new LocalHandler(coreThread.getLooper()));

       // startSocketService();
    }
    private boolean mStartException;
    public void startAndBindService(){
        try {
            startSocketService();
            bindSocketService();
            mStartException=false;
            MLog.getDefault().d("SOCKET","startAndBindSocketService Success");
        }catch (Exception e){
            e.printStackTrace();
            mStartException=true;
        }

    }
    public void bindSocketService(){
        Intent bindIntent = new Intent(context, WebSocketClientService.class);
        context.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopService(){
        try {
            /**
             * java.lang.RuntimeException:Unable to destroy activity {com.marryu/com.marryu.MainActivity}:
             * java.lang.IllegalArgumentException: Service not registered: com.wind.im.core.service.LocalAgent$1@e5e9c35
             */
            if (context!=null){
                if (serviceConnection!=null){
                    context.unbindService(serviceConnection);
                }
                context.stopService(mSocketServiceIntent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public synchronized static void init(Context context){
        if (sInstance==null)
            sInstance= new LocalAgent(context);
    }

    private static LocalAgent sInstance=null;
    public static LocalAgent getInstance(){

        return sInstance;
    }
    Intent mSocketServiceIntent;
    /**
     * 启动socket 长链接
     */
    private void startSocketService() {
        mSocketServiceIntent = new Intent(context, WebSocketClientService.class);
        //context.startService(mSocketServiceIntent);
        startService(context,mSocketServiceIntent,false);
    }
    /**
     * api = 26  O
     * Android8.0之后开启service时报错IllegalStateException: Not allowed to start service Intent ...
     */
    public static void startService(Context context, Intent service, boolean foreground) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && foreground) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }
    }

/*
    public void login(){
    }

    public void logout(){
    }*/


    /**
     * 发送数据给SocketService
     * @param packetData
     */
    public void send(PacketData packetData) {
       // SocketProxy.getInstance().sendMsg(packetData.getData());
        //Log.e(TAG, "serverMessenger==null" +(serverMessenger==null));
        while (serverMessenger==null && !mStartException){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (serverMessenger==null){
            return;
        }
        Message message = android.os.Message.obtain(null, Rpc.WHAT_BYTES_FROM_CLIENT);
        Bundle bundle=new Bundle();
        bundle.putByteArray(Rpc.KEY_BYTES,packetData.getData());
        message.setData(bundle);
        message.replyTo=replyTo;
        try {
            //Log.e(TAG, "send request");
            serverMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //Log.e(TAG, "ServiceConnection-->" + System.currentTimeMillis());
            //通过服务端返回的Binder创建Messenger
            serverMessenger = new Messenger(iBinder);
            //创建消息，通过Bundle传递数据
            Message message = Message.obtain(null, Rpc.WHAT_CLIENT_MESSENGER);
            message.replyTo=replyTo;
            try {
                //向服务端发送消息
                serverMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    /**
     * 处理服务端发送过来的消息
     */
    public class LocalHandler extends Handler{
        public LocalHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Rpc.WHAT_ACTION_ON_MESSAGE:
                    Bundle bundle=msg.getData();
                    final byte[] bytes=bundle.getByteArray(Rpc.KEY_BYTES);
                    if (bytes==null|| bytes.length==0){
                        System.out.println("接收到bytes为空");
                        return;
                    }
                    Handlers.mainHandler(context).post(new Runnable() {
                        @Override
                        public void run() {
                            ResponseParser.parseResponse(bytes);
                        }
                    });

                    break;
                case Rpc.WHAT_ACTION_ON_OPEN:
                    Observers.getInstance().dispatchOpened();
                    break;
            }
        }
    }
}



