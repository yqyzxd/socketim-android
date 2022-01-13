package com.wind.im.ui.viewholder;


import com.wind.im.R;
import com.wind.im.ui.BaseMultiItemFetchLoadAdapter;

/**
 * Created by zhoujianghua on 2015/8/6.
 */
public class MsgViewHolderUnknown extends MsgViewHolderBase {

    public MsgViewHolderUnknown(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_unknown;
    }

    @Override
    protected boolean isShowHeadImage() {
      /*  if (message.getSessionType() == SessionTypeEnum.ChatRoom) {
            return false;
        }*/
        return true;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
    }
}
