package com.wind.im.websocket;

import android.os.Handler;
import android.util.Log;

import com.wind.im.IMContext;
import com.wind.mlog.MLog;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * created by wind on 2020/4/14:2:01 PM
 */
public class WebSocketClientProxy implements IWebSocketClient{

    private IWebSocketClient impl;
    public WebSocketClientProxy(OnSocketStateChangeListener listener){
        this();
        this.mOnSocketStateChangeListener=listener;
    }
    public WebSocketClientProxy(){
        impl=new OKhttpWebSocketClient(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                //System.out.println("OKhttpWebSocketClient onOpen");
                onWebSocketOpened();
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                onReceivedMessage(bytes.toByteArray());

                if (mOnSocketStateChangeListener!=null){
                    mOnSocketStateChangeListener.onSocketStateChange(WebSocketState.ONMESSAGE,bytes.toByteArray());
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                if (mOnSocketStateChangeListener!=null){
                    mOnSocketStateChangeListener.onSocketStateChange(WebSocketState.CLOSED,code);
                }
            }
        });
    }
    public void initSocketClient() {
        String ws= IMContext.getOptions().ws;//C.Api.getWebSocketUrl();
        //MLog.getDefault().e("WebSocketClientProxy","ws:"+ws);

        connect(ws);
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
    }
    private void onReceivedMessage(byte[] bytes){
        //String hex= ByteUtil.getHexStr(bytes,bytes.length);
        //System.out.println("onReceivedMessage:"+hex);
    }
    private void onWebSocketOpened(){
        if (mOnSocketStateChangeListener!=null){
            mOnSocketStateChangeListener.onSocketStateChange(WebSocketState.OPENED,null);
        }
    }
    @Override
    public void connect(final String ws) {
        new Thread() {
            @Override
            public void run() {

                //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                if (impl!=null)
                    impl.connect(ws);

            }
        }.start();

    }

    @Override
    public void send(String msg) {
        impl.send(msg);
    }

    @Override
    public void send(byte[] bytes) {
        if (null != impl) {
            Log.e("JWebSocketClientService", "发送了消息：" + bytes.length);
            impl.send(bytes);
        }else {
            reconnectWs();
            Log.e("JWebSocketClientService", "没有连接到socket：" + bytes.length);
        }
    }

    @Override
    public boolean isClosed() {
        return impl.isClosed();
    }

    @Override
    public void reconnect() {
        reconnectWs();
    }

    @Override
    public void closeWebSocket() {
        if (null != impl) {
            impl.closeWebSocket();
        }
    }

    @Override
    public void destroy() {
        MLog.getDefault().d("SOCKET","WebSocketClientService onDestroy");
        mQuit=true;
        mHandler.removeCallbacksAndMessages(null);
        closeConnect();
    }

    private boolean mReconnecting;
    /**
     * 开启重连
     */
    private void reconnectWs() {

        if (mReconnecting){
            return;
        }
        mReconnecting=true;
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    if (!mQuit) {
                        MLog.getDefault().d("SOCKET","WebSocketClientService 开启重连");
                        if (impl!=null)
                            impl.reconnect();
                    }
                } finally {
                    mReconnecting=false;
                }
            }
        }.start();
    }
    private boolean mQuit;


    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            if (null != impl) {
                impl.closeWebSocket();
                impl.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            impl = null;
        }
    }
    //    -------------------------------------websocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE =10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
          /*  if (Globe.sDebug)
                LogUtil.e("JWebSocketClientService", "心跳包检测websocket连接状态 client.isClosed()"+impl.isClosed());
*/


            dumpPingPongCount();


            if (impl != null) {
                if (impl.isClosed()) {
                    reconnectWs();
                }

            } else {
                //如果client已为空，重新初始化连接
                impl = null;
                initSocketClient();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    private void dumpPingPongCount() {
        try {
            StringBuilder sBuilder=new StringBuilder();
            sBuilder.append("心跳包检测websocket连接状态 client.isClosed():")
                    .append(impl.isClosed())
                    .append("  ");
            if (impl!=null && impl instanceof OKhttpWebSocketClient){
                OKhttpWebSocketClient client= (OKhttpWebSocketClient) impl;
                int sentPingCount=client.getSentPingCount();
                int receivedPongCount=client.getReceivedPongCountMethod();
                int receivedPingCount=client.getReceivedPingCountMethod();
                /*System.out.println("sentPingCount:"+sentPingCount
                        +" receivedPongCount:"+receivedPongCount
                        +"receivedPingCount "+receivedPingCount);*/

                sBuilder.append("sentPingCount:").append(sentPingCount)
                        .append(" receivedPongCount:"+receivedPongCount)
                        .append(" receivedPingCount:"+receivedPingCount);
            }

           // MLog.getDefault().d("SOCKET",sBuilder.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private OnSocketStateChangeListener mOnSocketStateChangeListener;
    public void setOnSocketStateChangeListener(OnSocketStateChangeListener listener){
        this.mOnSocketStateChangeListener=listener;
    }
    public interface OnSocketStateChangeListener{
        void onSocketStateChange(WebSocketState state,Object data);
    }
}
