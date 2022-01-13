package com.wind.im.core;

public interface Observer<T> {

    void onEvent(T t);

}
