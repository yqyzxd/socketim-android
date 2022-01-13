package com.wind.im.service;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;


import com.wind.im.websocket.WebSocketClientProxy;
import com.wind.im.websocket.WebSocketState;
import com.wind.mlog.MLog;


public class WebSocketClientService extends Service implements WebSocketClientProxy.OnSocketStateChangeListener {

    private final static int FOREGROUND_SERVICE_ID = 2020;
    private Rpc mRpc=new Rpc();
    private WebSocketClientProxy mWebSocketClientProxy;

    @Override
    public void onSocketStateChange(WebSocketState state,Object data) {

        switch (state){
            case OPENED:
                //System.out.println("onSocketStateChange sendSocketOpenedMessage");
                mRpc.sendSocketOpenedMessage();
                break;
            case ONMESSAGE:
                mRpc.sendToClient((byte[]) data);
                break;
        }
    }






    @Override
    public IBinder onBind(Intent intent) {
        return mRpc.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(this,"socketService","MarryU",FOREGROUND_SERVICE_ID,false);
    }
   /**
        * api = 26  O
     * 与startForegroundService 配套使用
     * android.app.RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground(): ServiceRecord{}
     *
             * @param context
     * @param channelId
     * @param channelName
     * @param notificationId
     */
    public static void startForeground(Service context, String channelId, String channelName, int notificationId, boolean foreground) {
        //适配8.0service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && foreground) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context.getApplicationContext(), channelId).build();
            context.startForeground(notificationId, notification);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化websocket
        initSocketClient();



        //acquireWakeLock ();
        return START_STICKY_COMPATIBILITY;
    }


    @Override
    public void onDestroy() {
        if (mWebSocketClientProxy!=null){
            mWebSocketClientProxy.destroy();
        }

        super.onDestroy();
    }




    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        //LogUtil.e("JWebSocketClientService","initSocketClient");
        MLog.getDefault().d("SOCKET","initSocketClient");
        mWebSocketClientProxy=new WebSocketClientProxy(this);
        mWebSocketClientProxy.initSocketClient();
        mRpc.setWebSocketClient(mWebSocketClientProxy);

    }











//    -----------------------------------消息通知--------------------------------------------------------

    /**
     * 检查锁屏状态，如果锁屏先点亮屏幕
     *
     * @param content
     */
    private void checkLockAndShowNotification(String content) {
        //管理锁屏的一个服务
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {//锁屏
            //获取电源管理器对象
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (!pm.isScreenOn()) {
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
                wl.acquire();  //点亮屏幕
                wl.release();  //任务结束后释放
            }
            sendNotification(content);
        } else {
            sendNotification(content);
        }
    }

    /**
     * 发送通知
     *
     * @param content
     */
    private void sendNotification(String content) {
       /* Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                // 设置该通知优先级
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("服务器")
                .setContentText(content)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                // 向通知添加声音、闪灯和振动效果
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .build();
        notifyManager.notify(1, notification);//id要保证唯一*/
    }




}
