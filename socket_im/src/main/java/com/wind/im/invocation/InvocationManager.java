package com.wind.im.invocation;

import android.content.Context;

import com.wind.im.core.service.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By wind
 * on 2020/4/12
 */
public class InvocationManager {

    private static InvocationManager instance;
    private TransactionExecutor executor;
    private Map<String,TransactionFuture> futures=new HashMap<>();
    private ObserverManager observerManager;
    private InvocationManager(Context context){
         executor=new TransactionExecutor();
         observerManager=new ObserverManager();
    }

    public static void init(Context context){
        if (instance==null){
            instance=new InvocationManager(context);
        }
    }

    public static Object invoke(Transaction transaction) {
        checkInitialized();
        return instance.invokeIn(transaction);
    }

    public static boolean abort(Transaction transaction) {
        return false;
    }

    private Object invokeIn(Transaction transaction) {
        if (observerManager.process(transaction)){
            return null;
        }

        if (transaction.isSync()){//同步
            return executor.execute(transaction);
        }else {
            synchronized (futures){
                //异步
                TransactionFuture future=new TransactionFuture(transaction);
                futures.put(String.valueOf(transaction.getId()),future);
                executor.enqueue(transaction);
                return future;
            }
        }

    }

    private static void checkInitialized() {
        if (instance==null){
            throw new IllegalStateException("do you have call init method?");
        }
    }
}
