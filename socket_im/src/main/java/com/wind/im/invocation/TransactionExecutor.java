package com.wind.im.invocation;

import android.os.Handler;

import com.wind.im.core.service.MsgService;
import com.wind.im.core.service.MsgServiceRemote;
import com.wind.im.core.service.ServiceRemoteImpl;
import com.wind.im.core.service.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By wind
 * on 2020/4/12
 */
public class TransactionExecutor {

    public static final String TAG = "TransactionExecutor";

    public class Service {
        private Object impl;
        private Map<String, Method> methods=new HashMap<>();

        public Service(Object impl) {
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

        public Object invoke(Transaction transaction) throws InvocationTargetException, IllegalAccessException {
            Method method = methods.get(getMethodSignature(transaction.getMethod()));
            return method.invoke(impl, transaction.getArgs());
        }
    }


    private final Handler handler = Handlers.sharedInstance().newHandler("bk_executor");
    private final Map<String, Service> services = new HashMap<>();

    TransactionExecutor() {
        newService(MsgService.class, MsgServiceRemote.class);
    }

    private void newService(Class serviceInterfaceClass, Class serviceImplClass) {
        try {
            Object impl = serviceImplClass.newInstance();
            services.put(serviceInterfaceClass.getCanonicalName(), new Service(impl));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    /**
     * 同步
     *
     * @param transaction
     * @return
     */
    public Object execute(Transaction transaction) {
        //Transaction 中如何获取到Service？
        Service service = getService(transaction);
        if (service == null) {
            return null;
        }

        ServiceRemoteImpl.setTransaction(transaction);
        try {
            return service.invoke(transaction);
        } catch (Throwable tr) {
            if (tr instanceof InvocationTargetException && tr.getCause() != null) {
                tr = tr.getCause();
            }
            transaction.setException(tr).done();
            return null;
        } finally {
            ServiceRemoteImpl.clearTransaction();
        }

    }

    private Service getService(Transaction transaction) {
        Class declaringClass = transaction.getMethod().getDeclaringClass();
        //LogUtil.e(TAG, "declaringClass:" + declaringClass.getCanonicalName());
        Service service = services.get(declaringClass.getCanonicalName());
        return service;
    }

    /**
     * 异步
     *
     * @param transaction
     */
    public void enqueue(final Transaction transaction) {
        handler.post(new Runnable() {
            @Override
            public void run() {
               execute(transaction);
            }
        });
    }
}
