package com.wind.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MessageReceiver extends BroadcastReceiver {

    public static final String ACTION_ON_MESSAGE="action_on_message";
    public static final String ACTION_ON_OPEN="action_on_open";

    public static final String EXTRA_KEY_MESSAGE="extra_key_message";
    @Override
    public void onReceive(Context context, Intent intent) {

        String action=intent.getAction();
        if (action==null){
            return;
        }
        switch (action){
            case ACTION_ON_MESSAGE:
                //ResponseParser.parseResponse(intent.getByteArrayExtra(EXTRA_KEY_MESSAGE));
                break;
            case  ACTION_ON_OPEN:
                //Observers.getInstance().dispatchOpened();
                break;
        }
    }


}
