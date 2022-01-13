package com.wind.im.invocation;

/**
 * Created By wind
 * on 2020/4/12
 */
public interface InvocationFeture<T> {
    void setCallback(RequestCallback<T> callback);


    interface RequestCallback<T> {
        void onSuccess(T t);

        void onFailed(int code);

        void onException(Throwable e);
    }
}
