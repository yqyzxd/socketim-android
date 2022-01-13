package com.wind.im;

import android.content.Context;

import com.wind.im.bean.ImUserInfo;
import com.wind.im.bean.Options;
import com.wind.im.core.service.LocalAgent;
import com.wind.im.invocation.InvocationManager;
import com.wind.im.provider.IUserInfoProvider;
import com.wind.im.util.ProcessUtil;

public class IMContext {

    // 用户信息提供者
    private static IUserInfoProvider<ImUserInfo> sUserInfoProvider;
    private static Options sOptions;
    private static Context sAppContext;
   /* static {
        initUserInfoProvider(null);
        sOptions=new Options();
    }*/
    public static void init(Context context) {
        init(context, null, null);
    }
    public static void init(Context context,Options options) {
        init(context, options, null);
    }
    private static void init(Context context,Options options,IUserInfoProvider userInfoProvider){
        sAppContext=context.getApplicationContext();
        if (options==null){
            sOptions=new Options();
        }else {
            sOptions=options;
        }
        initUserInfoProvider(userInfoProvider);

        InvocationManager.init(context);
        if (ProcessUtil.isMainProcess(context)){
            LocalAgent.init(sAppContext);
        }

    }


    // 初始化用户信息提供者
    private static void initUserInfoProvider(IUserInfoProvider userInfoProvider) {

        if (userInfoProvider == null) {
            userInfoProvider = new BuildinUserInfoProvider();
        }

        IMContext.sUserInfoProvider = userInfoProvider;
    }

    public static IUserInfoProvider<ImUserInfo> getUserInfoProvider() {
        return sUserInfoProvider;
    }

    public static Options getOptions() {
        return sOptions;
    }

    public static Context getContext() {
        return sAppContext;
    }

    public static <T> T getService(Class<T> clazz) {
        return SDKCache.getInstance().getService(clazz);
    }
}
