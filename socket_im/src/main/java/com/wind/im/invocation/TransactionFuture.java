package com.wind.im.invocation;

import com.wind.im.core.response.ResponeCode;
import com.wind.im.core.service.Transaction;

/**
 * Created By wind
 * on 2020/4/12
 */
public class TransactionFuture implements AbortableFuture {
    private Transaction transaction;
    private RequestCallback callback;
    TransactionFuture(Transaction transaction){
        this.transaction=transaction;
    }
    @Override
    public void setCallback(RequestCallback callback) {
        this.callback=callback;
    }
    @Override
    public boolean abort() {
        return InvocationManager.abort(transaction);
    }
    public final void setResult(int result,Object reply){
        transaction.setResult(result);
        transaction.setReply(reply);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public final void done(){
        if (callback==null){
            return;
        }
        int result=transaction.getResult();
        Object reply=transaction.getReply();
        if (result== ResponeCode.RES_SUCCESS){
            callback.onSuccess(result);
        }else if (reply instanceof Throwable){
            callback.onException((Throwable) reply);
        }else {
            callback.onFailed(result);
        }
    }


}
