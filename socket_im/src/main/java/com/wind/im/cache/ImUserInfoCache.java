package com.wind.im.cache;

import android.text.TextUtils;

import com.wind.im.bean.ImUserInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImUserInfoCache {

    static class LazyHolder{
        private final static ImUserInfoCache INSTANCE=new ImUserInfoCache();
    }

    public static ImUserInfoCache getInstance(){
        return LazyHolder.INSTANCE;
    }
    private Map<String, ImUserInfo> account2UserMap = new ConcurrentHashMap<>();




    /**
     * ******************************* 业务接口（获取缓存的用户信息） *********************************
     */
    public void put(ImUserInfo userInfo){
        if (userInfo==null || account2UserMap == null) {
            return ;
        }
        account2UserMap.put(userInfo.getAccount(),userInfo);
    }
    public ImUserInfo getUserInfo(String account) {
        if (TextUtils.isEmpty(account) || account2UserMap == null) {
            return null;
        }
        return account2UserMap.get(account);
    }

    private boolean hasUser(String account) {
        if (TextUtils.isEmpty(account) || account2UserMap == null) {
            return false;
        }
        return account2UserMap.containsKey(account);
    }


    private void clearUserCache() {
        account2UserMap.clear();
    }



}
