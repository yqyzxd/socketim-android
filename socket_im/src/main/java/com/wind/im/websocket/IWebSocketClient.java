package com.wind.im.websocket;

/**
 * created by wind on 2020/4/9:3:54 PM
 */
public interface IWebSocketClient {
    /**
     * 链接到指定地址的websocket服务
     * @param ws
     */
    void connect(String ws);

    /**
     * 发送字符串消息
     * @param msg
     */
    void send(String msg);

    /**
     * 发送字节数组
     * @param bytes
     */
    void send(byte[] bytes);

    /**
     * 连接是否已经关闭
     * @return
     */
    boolean isClosed();

    /**
     * 重新连接
     */
    void reconnect();

    /**
     * 关闭webSocket链路
     */
    void closeWebSocket();

    /**
     * 销毁所有资源
     */
    void destroy();
}
