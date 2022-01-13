package com.wind.im.ui;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.wind.im.IMContext;
import com.wind.im.R;
import com.wind.im.core.IMessage;
import com.wind.im.ui.viewholder.BaseViewHolder;
import com.wind.im.ui.viewholder.MsgViewHolderBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by huangjun on 2016/12/21.
 */
public class MsgAdapter extends BaseMultiItemFetchLoadAdapter<IMessage, BaseViewHolder> {

    private Map<Class<? extends MsgViewHolderBase>, Integer> holder2ViewType;

    private ViewHolderEventListener eventListener;
    private Map<String, Float> progresses; // 有文件传输，需要显示进度条的消息ID map
    private String messageId;
    private Container container;

    MsgAdapter(RecyclerView recyclerView, List<IMessage> data, Container container) {
        super(recyclerView, data);

        timedItems = new HashSet<>();
        progresses = new HashMap<>();

        // view type, view holder
        holder2ViewType = new HashMap<>();
        List<Class<? extends MsgViewHolderBase>> holders = MsgViewHolderFactory.getAllViewHolders();
        int viewType = 0;
        for (Class<? extends MsgViewHolderBase> holder : holders) {
            viewType++;
            addItemType(viewType, R.layout.im_message_item, holder);
            holder2ViewType.put(holder, viewType);
        }

        this.container = container;
//        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                Log.e("TAG","AdapterData Change....");
//            }
//        });
    }

    @Override
    protected int getViewType(IMessage message) {
        return holder2ViewType.get(MsgViewHolderFactory.getViewHolderByType(message));
    }

    @Override
    protected String getItemKey(IMessage item) {
        return item.getUuid();
    }

    public void setEventListener(ViewHolderEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public ViewHolderEventListener getEventListener() {
        return eventListener;
    }

    public void deleteItem(IMessage message, boolean isRelocateTime) {
        if (message == null) {
            return;
        }

        int index = 0;
        for (IMessage item : getData()) {
            if (item.isTheSame(message)) {
                break;
            }
            ++index;
        }

        if (index < getDataSize()) {
            remove(index);
            if (isRelocateTime) {
                relocateShowTimeItemAfterDelete(message, index);
            }
//            notifyDataSetChanged(); // 可以不要！！！
        }
    }

    public float getProgress(IMessage message) {
        Float progress = progresses.get(message.getUuid());
        return progress == null ? 0 : progress;
    }

    public void putProgress(IMessage message, float progress) {
        progresses.put(message.getUuid(), progress);
    }

    /**
     * *********************** 时间显示处理 ***********************
     */

    private Set<String> timedItems; // 需要显示消息时间的消息ID
    private IMessage lastShowTimeItem; // 用于消息时间显示,判断和上条消息间的时间间隔

    public boolean needShowTime(IMessage message) {
        return timedItems.contains(message.getUuid());
    }

    /**
     * 列表加入新消息时，更新时间显示
     */
    public void updateShowTimeItem(List<IMessage> items, boolean fromStart, boolean update) {
        IMessage anchor = fromStart ? null : lastShowTimeItem;
        for (IMessage message : items) {
            if (setShowTimeFlag(message, anchor)) {
                anchor = message;
            }
        }

        if (update) {
            lastShowTimeItem = anchor;
        }
    }

    /**
     * 是否显示时间item
     */
    private boolean setShowTimeFlag(IMessage message, IMessage anchor) {
        boolean update = false;

        if (hideTimeAlways(message)) {
            setShowTime(message, false);
        } else {
            if (anchor == null) {
                setShowTime(message, true);
                update = true;
            } else {
                long time = anchor.getTimestamp();
                long now = message.getTimestamp();

                if (now - time == 0) {
                    // 消息撤回时使用
                    setShowTime(message, true);
                    lastShowTimeItem = message;
                    update = true;
                } else if (now - time < (IMContext.getOptions().displayMsgTimeWithInterval)) {
                    setShowTime(message, false);
                } else {
                    setShowTime(message, true);
                    update = true;
                }
            }
        }

        return update;
    }

    private void setShowTime(IMessage message, boolean show) {
        if (show) {
            timedItems.add(message.getUuid());
        } else {
            timedItems.remove(message.getUuid());
        }
    }

    private void relocateShowTimeItemAfterDelete(IMessage messageItem, int index) {
        // 如果被删的项显示了时间，需要继承
        if (needShowTime(messageItem)) {
            setShowTime(messageItem, false);
            if (getDataSize() > 0) {
                IMessage nextItem;
                if (index == getDataSize()) {
                    //删除的是最后一项
                    nextItem = getItem(index - 1);
                } else {
                    //删除的不是最后一项
                    nextItem = getItem(index);
                }

                // 增加其他不需要显示时间的消息类型判断
                if (hideTimeAlways(nextItem)) {
                    setShowTime(nextItem, false);
                    if (lastShowTimeItem != null && lastShowTimeItem != null
                            && lastShowTimeItem.isTheSame(messageItem)) {
                        lastShowTimeItem = null;
                        for (int i = getDataSize() - 1; i >= 0; i--) {
                            IMessage item = getItem(i);
                            if (needShowTime(item)) {
                                lastShowTimeItem = item;
                                break;
                            }
                        }
                    }
                } else {
                    setShowTime(nextItem, true);
                    if (lastShowTimeItem == null
                            || (lastShowTimeItem != null && lastShowTimeItem.isTheSame(messageItem))) {
                        lastShowTimeItem = nextItem;
                    }
                }
            } else {
                lastShowTimeItem = null;
            }
        }
    }

    private boolean hideTimeAlways(IMessage message) {
       /* if (message.getSessionType() == SessionType.ChatRoom) {
            return true;
        }
        switch (message.getMsgType()) {
            case notification:
                return true;
            default:
                return false;
        }*/
       return false;
    }

    public interface ViewHolderEventListener {
        // 长按事件响应处理
        boolean onViewHolderLongClick(View clickView, View viewHolderView, IMessage item);

        // 发送失败或者多媒体文件下载失败指示按钮点击响应处理
        void onFailedBtnClick(IMessage resendMessage);

        // viewholder footer按钮点击，如机器人继续会话
        void onFooterClick(IMessage message);
    }

    public void setUuid(String messageId) {
        this.messageId = messageId;
    }

    public String getUuid() {
        return messageId;
    }

    public Container getContainer() {
        return container;
    }
}
