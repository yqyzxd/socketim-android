package com.wind.im.core;

import java.io.Serializable;

/**
 * created by wind on 2020/4/17:10:12 AM
 * 后期增加单独的im登录接口
 */
public class LoginInfo implements Serializable {

    private String account;
    private String token;
    private String appKey;
    private String network;
    private String os;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
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
