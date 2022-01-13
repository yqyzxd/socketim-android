package com.wind.im.core.service;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created By wind
 * on 2020/4/12
 */
public class Transaction {

    private int id;
    private Object[] args;
    private Method method;
    private boolean future;
    private boolean sync;


    private int result;
    private Object reply;
    public Transaction( Object[] args,Method method,boolean future,boolean sync){
        id=IdGenerator.generat();
        this.args=args;
        this.method=method;
        this.future=future;
        this.sync=sync;



    }

    public Object[] getArgs() {
        return args;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isFuture() {
        return future;
    }

    public boolean isSync() {
        return sync;
    }

    public int getId() {
        return id;
    }

    /**
     * 保存异步调用的线程Looper
     */
    public void saveAsyncInvokeThreadLooper(){

    }



    public Transaction setException(Throwable tr) {
        return this;
    }

    public void done() {

    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Object getReply() {
        return reply;
    }

    public void setReply(Object reply) {
        this.reply = reply;
    }

    private static final class IdGenerator {
        private static AtomicInteger atomicInteger = new AtomicInteger(0);

        public static int generat() {
            return atomicInteger.incrementAndGet();
        }
    }
}
