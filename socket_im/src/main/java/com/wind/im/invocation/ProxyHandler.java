package com.wind.im.invocation;

import com.wind.im.core.service.Transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By wind
 * on 2020/4/12
 */
public class ProxyHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //未来需要提供数据返回， void除外
        boolean future=!method.getReturnType().equals(void.class) && !method.getReturnType().equals(Void.class);

        boolean sync=future && !method.getReturnType().isAssignableFrom(TransactionFuture.class);

        Transaction transaction=new Transaction(args,method,future,sync);

        //
        transaction.saveAsyncInvokeThreadLooper();

        Object value=InvocationManager.invoke(transaction);
        return sync? DefaultPrimitive.nullCheck(method.getReturnType(),value):value;

    }

    private static class DefaultPrimitive{
        private static final Map<Class,Object> defaults=new HashMap<>(7);

        static {
            defaults.put(int.class,Integer.valueOf(0));
            defaults.put(long.class,Long.valueOf(0));
            defaults.put(boolean.class,Boolean.valueOf(false));
            defaults.put(byte.class,Byte.valueOf((byte) 0));
            defaults.put(float.class,Float.valueOf(0));
            defaults.put(double.class,Double.valueOf(0));
            defaults.put(char.class,Character.valueOf((char) 0));
        }

        private static Object nullCheck(Class clazz,Object value){
            if (value!=null || !defaults.containsKey(clazz)){
                return value;
            }
            return defaults.get(clazz);
        }
    }
}
