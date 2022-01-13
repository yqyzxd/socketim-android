package com.wind.im.core.service;

/**
 * Created By wind
 * on 2020/4/12
 */
public abstract class ServiceRemoteImpl{

    private static final ThreadLocal<Transaction> transactionCache=new ThreadLocal<>();

    public static final void setTransaction(Transaction transaction){
        transactionCache.set(transaction);
    }
    public static final void clearTransaction(){
        transactionCache.set(null);
    }

    protected Transaction transactionCache(){
        return transactionCache.get();
    }
}
