package com.wind.im.invocation;

import com.wind.im.core.service.MsgServiceObserve;
import com.wind.im.core.service.MsgServiceObserveRemote;
import com.wind.im.core.service.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * created by wind on 2020/4/13:10:10 AM
 */
public class ObserverManager {
    private Map<String, Observer> observers = new HashMap<>();

    ObserverManager() {
        newObserver(MsgServiceObserve.class, MsgServiceObserveRemote.class);
    }

    private void newObserver(Class observeInterfaceClass, Class<MsgServiceObserveRemote> observeImplClass) {
        try {
            Object impl = observeImplClass.newInstance();
            observers.put(observeInterfaceClass.getCanonicalName(), new ObserverManager.Observer(impl));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


    public boolean process(Transaction transaction) {
        Class clazz = transaction.getMethod().getDeclaringClass();
        Observer observer=observers.get(clazz.getCanonicalName());

        if (observer==null){
            return false;
        }
        //执行注册
        try {
            observer.process(transaction);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return true;
    }


    public static class Observer {
        private Object impl;
        private Map<String, Method> methods=new HashMap<>();
        public Observer(Object impl) {
            this.impl = impl;
            //获取impl中的所有方法
            Method[] methodArr = impl.getClass().getMethods();
            for (Method method : methodArr) {
                methods.put(getMethodSignature(method), method);
            }
        }

        private String getMethodSignature(Method method) {
            StringBuilder signatureBuilder = new StringBuilder();
            signatureBuilder.append(method.getName());
            for (Class clazz : method.getParameterTypes()) {
                signatureBuilder.append("_").append(clazz.getSimpleName());
            }
            return signatureBuilder.toString();
        }

        public Object process(Transaction transaction) throws InvocationTargetException, IllegalAccessException {
            Method method = methods.get(getMethodSignature(transaction.getMethod()));
            return method.invoke(impl, transaction.getArgs());
        }
    }
}
