package com.wind.im.invocation;

import java.lang.reflect.Proxy;

/**
 * Created By wind
 * on 2020/4/12
 */
public class ProxyServiceFactory {

    public static Object newService(Class clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new ProxyHandler());
    }
}
