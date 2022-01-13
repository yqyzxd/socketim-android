package com.wind.im.websocket;

import com.wind.mlog.MLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OKhttpWebSocketClient implements IWebSocketClient{
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private OkHttpClient sClient;
    private WebSocket sWebSocket;
    private WebSocketListener mWebSocketListener;
    private String ws;
    private boolean mClosed;
    private Method sentPingCountMethod;
    private Method receivedPongCountMethod;
    private Method receivedPingCountMethod;
    private Field failedField;
    public int getSentPingCount() {
        try {
            if (sentPingCountMethod!=null && sWebSocket!=null){
                return (int) sentPingCountMethod.invoke(sWebSocket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getReceivedPongCountMethod() {
        try {
            if (receivedPongCountMethod!=null&& sWebSocket!=null)
                return (int) receivedPongCountMethod.invoke(sWebSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getReceivedPingCountMethod() {
        try {
            if (receivedPingCountMethod!=null&& sWebSocket!=null)
                return (int) receivedPingCountMethod.invoke(sWebSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public OKhttpWebSocketClient(WebSocketListener webSocketListener) {
        this.mWebSocketListener = webSocketListener;
    }

    public synchronized void connect(String ws) {
        this.ws = ws;
        if (sClient == null) {
            sClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//允许失败重试
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .pingInterval(10000,TimeUnit.MILLISECONDS) //客户端开启ping 如果服务端没有回复pong 那么客户端会主动断开
                    .build();


        }
        if (sWebSocket == null) {
            //ws://echo.websocket.org
            Request request = new Request.Builder().url(ws).build();

            EchoWebSocketListener listener = new EchoWebSocketListener(mWebSocketListener);
            sWebSocket = sClient.newWebSocket(request, listener);

            try {
                sentPingCountMethod= sWebSocket.getClass().getDeclaredMethod("sentPingCount");
                receivedPongCountMethod= sWebSocket.getClass().getDeclaredMethod("receivedPongCount");
                receivedPingCountMethod= sWebSocket.getClass().getDeclaredMethod("receivedPingCount");
                sentPingCountMethod.setAccessible(true);
                receivedPongCountMethod.setAccessible(true);
                receivedPingCountMethod.setAccessible(true);

                //  private boolean failed;
                failedField=sWebSocket.getClass().getDeclaredField("failed");
                failedField.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void send(String msg) {
    }

    @Override
    public void send(byte[] bytes) {
        WebSocket webSocket;
        synchronized (OKhttpWebSocketClient.class) {
            webSocket = sWebSocket;
        }
        if (webSocket != null) {
            sendMessage(webSocket, bytes);
        }
    }
    private static void sendMessage(WebSocket webSocket, byte[] bytes) {
        boolean enqueued=webSocket.send(ByteString.of(bytes));
        System.out.println("sendMessage enqueued:"+enqueued);

    }
    /**
     * 关闭websocket
     */
    public synchronized void closeWebSocket() {
        if (sWebSocket != null) {
            sWebSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
            sWebSocket = null;
        }
    }

    /**
     * 关闭OkHttpClient
     */
    public synchronized void destroy() {
        if (sClient != null) {
            sClient.dispatcher().executorService().shutdown();
            sClient = null;
        }
    }

    /**
     * 重置websocket
     */
    private void resetWebSocket() {
        synchronized (OKhttpWebSocketClient.class) {
            sWebSocket = null;
            mClosed = true;
        }
    }
    @Override
    public boolean isClosed() {
        boolean failed=false;
        if (failedField!=null && sWebSocket!=null){
            try {
                failed= (boolean) failedField.get(sWebSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sWebSocket==null){
            failed=true;
        }
        //反射获取RealWebSocket的 failed
        return mClosed || failed;
    }

    /**
     * 重新连接websocket
     */
    public void reconnect() {
        synchronized (OKhttpWebSocketClient.class) {
            sWebSocket = null;
            connect(ws);
        }
    }

    public class EchoWebSocketListener extends WebSocketListener {
        private static final String TAG = "EchoWebSocketListener";
        private WebSocketListener mClientWebSocketListener;

        public EchoWebSocketListener(WebSocketListener clientWebSocketListener) {
            this.mClientWebSocketListener = clientWebSocketListener;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            //Log.i(TAG, "onOpen  response:" + response.toString());
            MLog.getDefault().d(TAG,  "onOpen  response:" + response.toString());
            mClosed = false;
            mClientWebSocketListener.onOpen(webSocket, response);

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
           // Log.i(TAG, "Receiving: " + text);
            MLog.getDefault().d(TAG, "Receiving: " + text);
            mClientWebSocketListener.onMessage(webSocket, text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
           // Log.i(TAG, "Receiving: " + bytes.hex());
            MLog.getDefault().d(TAG, "Receiving: " + bytes.hex());
            mClientWebSocketListener.onMessage(webSocket, bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            //Log.i(TAG, "Closing: " + code + " " + reason);
            MLog.getDefault().d(TAG, "Closing: " + code + " " + reason);
            resetWebSocket();
            mClientWebSocketListener.onClosing(webSocket, code, reason);

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
           // Log.i(TAG, "Closed: " + code + " " + reason);
            MLog.getDefault().d(TAG,"Closed: " + code + " " + reason);
            mClientWebSocketListener.onClosed(webSocket, code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            //Log.i(TAG, "onFailure: ");
            String responseMsg="";
            if (response!=null){
                responseMsg=response.toString();
            }
            MLog.getDefault().d(TAG,t,"onFailure: "+ responseMsg);
            t.printStackTrace();
            resetWebSocket();
            mClientWebSocketListener.onFailure(webSocket, t, response);
        }
    }
}