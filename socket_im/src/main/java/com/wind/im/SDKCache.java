package com.wind.im;

import com.wind.im.invocation.ServiceManager;

/**
 * Created By wind
 * on 2020/4/12
 */
public class SDKCache {
    private ServiceManager mServiceManager;
    public static SDKCache getInstance(){
        return InstanceHolder.sInstance;
    }
    private static class InstanceHolder{
        private static final SDKCache sInstance=new SDKCache();
    }

    public SDKCache(){
        mServiceManager=new ServiceManager();
    }

    public <T> T getService(Class<T> clazz){
        return mServiceManager.getService(clazz);
    }
}
