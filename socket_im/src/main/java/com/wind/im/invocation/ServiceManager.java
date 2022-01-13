package com.wind.im.invocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By wind
 * on 2020/4/12
 */
public class ServiceManager {

    private final Map<Class<?>,Object> services=new HashMap<>();

    public <T> T getService(Class<T> clazz){
        if (!clazz.isInterface()){
            throw  new IllegalArgumentException("only accept interface:"+clazz);
        }
        Object service;
        synchronized (services){
            service=services.get(clazz);
            if (service==null){
                service=ProxyServiceFactory.newService(clazz);
                services.put(clazz,service);
            }
        }
        return (T) service;
    }
}
