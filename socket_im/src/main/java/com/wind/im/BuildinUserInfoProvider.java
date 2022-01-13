package com.wind.im;

import com.wind.im.bean.ImUserInfo;
import com.wind.im.cache.ImUserInfoCache;
import com.wind.im.callback.SimpleCallback;
import com.wind.im.provider.IUserInfoProvider;

import java.util.List;

public class BuildinUserInfoProvider implements IUserInfoProvider<ImUserInfo> {

    @Override
    public ImUserInfo getUserInfo(String account) {
        ImUserInfo user = ImUserInfoCache.getInstance().getUserInfo(account);
        if (user == null) {
            //todo get from remote
           // NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
        }
        return user;
    }

    @Override
    public List<ImUserInfo> getUserInfo(List<String> accounts) {
        return null;
    }

    @Override
    public void getUserInfoAsync(String account, SimpleCallback<ImUserInfo> callback) {

    }

    @Override
    public void getUserInfoAsync(List<String> accounts, SimpleCallback<List<ImUserInfo>> callback) {

    }
}
