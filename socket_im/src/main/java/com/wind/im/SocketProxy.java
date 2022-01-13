package com.wind.im;

import android.content.Context;

public class SocketProxy {
    private Context mContext;
   /* private SocketProxy(Context context){
        this.mContext=context;
        startSocketService();
        bindSocketService();
    }

    private static SocketProxy sInstance=null;
    public static SocketProxy getInstance(){
        return sInstance;
    }
    public static void init(Context context){
        if (sInstance==null){
            synchronized (SocketProxy.class){
                if (sInstance==null){
                    sInstance=new SocketProxy(context);
                }
            }
        }
    }
    private Intent mSocketServiceIntent;

    private void startSocketService() {
        mSocketServiceIntent = new Intent(mContext, WebSocketClientService2.class);
        mContext.startService(mSocketServiceIntent);
    }

    private void bindSocketService() {
        Intent bindIntent = new Intent(mContext, WebSocketClientService.class);
        mContext.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public void closeSocket(){
        mContext.unbindService(serviceConnection);
        mContext.stopService(mSocketServiceIntent);
        sInstance=null;
    }

    private WebSocketClientService2 jWebSClientService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("JWebSocketClientService", "服务与活动成功绑定");
            WebSocketClientService2.JWebSocketClientBinder  binder = (WebSocketClientService2.JWebSocketClientBinder) iBinder;

            jWebSClientService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("JWebSocketClientService", "服务与活动成功断开");
        }
    };



    public void sendMsg(byte[] bytes){
        if (jWebSClientService!=null) {
            jWebSClientService.sendMsg(bytes);
        }
    }
    public void sendMsg(CommandType commandType, byte[] bytes){
        if (jWebSClientService!=null) {
            jWebSClientService.sendMsg(SocketCommand.ofCmd(commandType.getValue(),bytes).getCommandBytes());
        }
    }*/
}
