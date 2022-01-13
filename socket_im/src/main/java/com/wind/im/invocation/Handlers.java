package com.wind.im.invocation;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created By wind
 * on 2020/4/12
 */
public class Handlers {
    private static Handlers sInstance=null;
    private Handlers(){
    }
    public static synchronized Handlers sharedInstance() {
        if (sInstance==null){
            sInstance=new Handlers();
        }
        return sInstance;
    }


    private final HashMap<String, HandlerThread> handlerThreads = new HashMap();
    private static Handler mainHandler;

    public static Handler mainHandler(Context context) {
        if (mainHandler == null) {
            mainHandler = new Handler(context.getMainLooper());
        }
        return mainHandler;
    }

    public final Handler newHandler() {
        return this.newHandler("DEFAULT");
    }

    public final Handler misc() {
        return this.newHandler("MISC");
    }

    public static Handler newMainHandler(Context context) {
        return new Handler(context.getMainLooper());
    }

    public final Handler newHandler(String name) {
        return new Handler(this.handlerThread(name).getLooper());
    }

    private HandlerThread handlerThread(String name) {
        synchronized(this.handlerThreads) {
            HandlerThread handlerThread;
            if ((handlerThread = (HandlerThread)this.handlerThreads.get(name)) != null && handlerThread.getLooper() == null) {
                this.handlerThreads.remove(name);
                handlerThread = null;
            }

            if (handlerThread == null) {
                (handlerThread = new HandlerThread("NIM-HT-" + (TextUtils.isEmpty(name) ? "DEFAULT" : name))).start();
                this.handlerThreads.put(name, handlerThread);
            }

            return handlerThread;
        }
    }



}
