package com.wind.im.ui;

import android.app.Activity;

import com.wind.im.IMSession;

/**
 * Created by zhoujianghua on 2015/7/6.
 */
public class Container {

    public final Activity activity;


    public final ModuleProxy proxy;

    public final boolean proxySend;

    public final IMSession session;

    public Container(Activity activity, IMSession session, ModuleProxy proxy) {
        this(activity, session, proxy, false);
    }

    public Container(Activity activity, IMSession session, ModuleProxy proxy,
                     boolean proxySend) {
        this.activity = activity;
        this.session=session;
        this.proxy = proxy;
        this.proxySend = proxySend;
    }
}
