package com.wind.im.core;

/**
 * created by wind on 2020/4/14:2:36 PM
 */
public class LoginMessage {

    private String token;
    private String network;
    private String os;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
