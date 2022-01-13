package com.wind.im.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.wind.im.R;
import com.wind.im.core.IMessage;
import com.wind.im.core.MsgDirection;
import com.wind.im.core.SessionType;
import com.wind.im.ui.widget.MsgListFetchLoadMoreView;
import com.wind.im.util.BitmapDecoder;
import com.wind.im.util.ScreenUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 基于RecyclerView的消息收发模块
 * Created by huangjun on 2016/12/27.
 */
public class MessageListPanelEx {
    private final static String TAG = "MessageListPanelEx";

    private static final int REQUEST_CODE_FORWARD_PERSON = 0x01;
    private static final int REQUEST_CODE_FORWARD_TEAM = 0x02;

    // container
    private Container container;
    private View rootView;

    // message list view
    private RecyclerView messageListView;
    private List<IMessage> items;
    private MsgAdapter adapter;
    private ImageView ivBackground;

    // 新消息到达提醒
    //private IncomingMsgPrompt incomingMsgPrompt;
    private Handler uiHandler;

    // 仅显示消息记录，不接收和发送消息
    private boolean recordOnly;
    // 从服务器拉取消息记录
    private boolean remote;

    // 语音转文字
   // private VoiceTrans voiceTrans;

    // 待转发消息
    private IMessage forwardMessage;

    // 背景图片缓存
    private static Pair<String, Bitmap> background;

    //如果在发需要拍照 的消息时，拍照回来时页面可能会销毁重建，重建时会在MessageLoader 的构造方法中调一次 loadFromLocal
    //而在发送消息后，list 需要滚动到底部，又会通过RequestFetchMoreListener 调用一次 loadFromLocal
    //所以消息会重复
    private boolean mIsInitFetchingLocal;

    public MessageListPanelEx(Container container, View rootView, boolean recordOnly, boolean remote) {
        this(container, rootView, null, recordOnly, remote);
    }

    public MessageListPanelEx(Container container, View rootView, IMessage anchor, boolean recordOnly, boolean remote) {
        this.container = container;
        this.rootView = rootView;
        this.recordOnly = recordOnly;
        this.remote = remote;

        init(anchor);
    }

    public List<IMessage> getItems() {
        return items;
    }

    public void onResume() {
        //setEarPhoneMode(UserPreferences.isEarPhoneModeEnable(), false);
    }

    public void onPause() {
       // MessageAudioControl.getInstance(container.activity).stopAudio();
    }

    public void onDestroy() {
        registerObservers(false);
    }

    public boolean onBackPressed() {
        uiHandler.removeCallbacks(null);
      /*  MessageAudioControl.getInstance(container.activity).stopAudio(); // 界面返回，停止语音播放
        if (voiceTrans != null && voiceTrans.isShow()) {
            voiceTrans.hide();
            return true;
        }*/
        return false;
    }

    public void reload(Container container, IMessage anchor) {
        this.container = container;
        if (adapter != null) {
            adapter.clearData();
        }
        initFetchLoadListener(anchor);
    }

    private void init(IMessage anchor) {
        initListView(anchor);

        this.uiHandler = new Handler();
        if (!recordOnly) {
            //incomingMsgPrompt = new IncomingMsgPrompt(container.activity, rootView, messageListView, adapter, uiHandler);
        }

        registerObservers(true);
    }

    private void initListView(IMessage anchor) {
        ivBackground = rootView.findViewById(R.id.message_activity_background);

        // RecyclerView
        messageListView = rootView.findViewById(R.id.messageListView);

        messageListView.setLayoutManager(new LinearLayoutManager(container.activity));
        messageListView.requestDisallowInterceptTouchEvent(true);
        messageListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    container.proxy.shouldCollapseInputPanel();
                }
            }
        });
        messageListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // adapter
        items = new ArrayList<>();
        adapter = new MsgAdapter(messageListView, items, container);
        adapter.setFetchMoreView(new MsgListFetchLoadMoreView());
        adapter.setLoadMoreView(new MsgListFetchLoadMoreView());
      //  adapter.setEventListener(new MsgItemEventListener());
        initFetchLoadListener(anchor);
        messageListView.setAdapter(adapter);
      //  messageListView.addOnItemTouchListener(listener);
    }

  /*  private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(IRecyclerView adapter, View view, int position) {

        }

        @Override
        public void onItemLongClick(MotionEvent e,IRecyclerView adapter, View view, int position) {
        }

        @Override
        public void onItemChildClick(IRecyclerView adapter2, View view, int position) {

            if (!isSessionMode() || !(view instanceof RobotLinkView)) {
                return;
            }

            RobotLinkView robotLinkView = (RobotLinkView) view;
            LinkElement element = robotLinkView.getElement();
            if (element == null) {
                return;
            }
            if (LinkElement.TYPE_URL.equals(element.getType())) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(element.getTarget());
                intent.setData(content_url);
                try {
                    container.activity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    ToastHelper.showToast(container.activity, "路径错误");
                }
            } else if (LinkElement.TYPE_BLOCK.equals(element.getType())) {
                // 发送点击的block
                IMMessage message = adapter.getItem(position);
                if (message != null) {
                    String robotAccount = ((RobotAttachment) message.getAttachment()).getFromRobotAccount();
                    IMMessage robotMsg = MessageBuilder.createRobotMessage(message.getSessionId(),
                            message.getSessionType(),
                            robotAccount,
                            robotLinkView.getShowContent(),
                            RobotMsgType.LINK,
                            "",
                            element.getTarget(),
                            element.getParams());
                    container.proxy.sendMessage(robotMsg);
                }
            }
        }
    };*/

    public boolean isSessionMode() {
        return !recordOnly && !remote;
    }

    private void initFetchLoadListener(IMessage anchor) {
        /*MessageLoader loader = new MessageLoader(anchor, remote);
        if (recordOnly && !remote) {
            // 双向Load
            adapter.setOnFetchMoreListener(loader);
            adapter.setOnLoadMoreListener(loader);
        } else {
            // 只下来加载old数据
            adapter.setOnFetchMoreListener(loader);
        }*/
    }

    // 刷新消息列表
    public void refreshMessageList() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void scrollToBottom() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doScrollToBottom();
            }
        }, 200);
    }

    private void doScrollToBottom() {
        messageListView.scrollToPosition(adapter.getBottomDataPosition());

        List<IMessage> reloadMsgs = items;
        int reloadSize=reloadMsgs.size();
        int reveivedCount=0;
        int sendCount=0;
        int count=Math.min(50,reloadSize);
        for (int i=0;i<count;i++){
            IMessage message=reloadMsgs.get(i);
            if (message.getDirect()== MsgDirection.In){
                if (reveivedCount<5){
                    reveivedCount++;
                }

            }else {
                if (sendCount<5){
                    sendCount++;
                }
            }

        }
        int progress=reveivedCount+sendCount;

        try {
            Class clazz=Class.forName("com.wind.base.utils.RxBus");
            Method method=clazz.getDeclaredMethod("getInstance");
            Object instance=method.invoke(null);

            Method postMethod=clazz.getDeclaredMethod("post",Object.class);
            postMethod.invoke(instance,new ProgressEvent(progress));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //RxBus.getInstance().post(new ProgressEvent(progress));


    }

    public static class ProgressEvent{
        private int progress;
        public ProgressEvent(int progress){
            this.progress=progress;
        }

        public int getProgress() {
            return progress;
        }
    }

    public void onIncomingMessage(List<IMessage> messages) {
        boolean needScrollToBottom = isLastMessageVisible();
        boolean needRefresh = false;
        List<IMessage> addedListItems = new ArrayList<>(messages.size());
        for (IMessage message : messages) {
            if (isMyMessage(message)) {
                items.add(message);
                addedListItems.add(message);
                needRefresh = true;
            }
        }
        if (needRefresh) {
            //wind 如何排序是个问题 先注释了？
            //sortMessages(items);
            adapter.notifyDataSetChanged();
        }

        adapter.updateShowTimeItem(addedListItems, false, true);

        // incoming messages tip
        IMessage lastMsg = messages.get(messages.size() - 1);
        if (isMyMessage(lastMsg)) {
            if (needScrollToBottom) {
                doScrollToBottom();
            } /*else if (incomingMsgPrompt != null && lastMsg.getSessionType() != SessionTypeEnum.ChatRoom) {
                incomingMsgPrompt.show(lastMsg);
            }*/
        }
    }

    private boolean isLastMessageVisible() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) messageListView.getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        return lastVisiblePosition >= adapter.getBottomDataPosition();
    }

    // 发送消息后，更新本地消息列表
    public void onMsgSend(IMessage message) {
        if (!container.session.getSessionId().equals(message.getSessionId())) {
            return;
        }
        List<IMessage> addedListItems = new ArrayList<>(1);
        addedListItems.add(message);
        adapter.updateShowTimeItem(addedListItems, false, true);

        adapter.appendData(message);

        doScrollToBottom();

        System.out.println("onMsgSend count:"+adapter.getItemCount());
    }

    /**
     * **************************** 排序 ***********************************
     */
    private void sortMessages(List<IMessage> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    private static Comparator<IMessage> comp = new Comparator<IMessage>() {

        @Override
        public int compare(IMessage o1, IMessage o2) {
            System.out.println("o1.getTimestamp():"+o1.getTimestamp());
            System.out.println("o2.getTimestamp():"+o2.getTimestamp());
            long time = o1.getTimestamp() - o2.getTimestamp();
            return time == 0 ? 0 : (time < 0 ? -1 : 1);
        }
    };

    /**
     * 消息状态变化观察者
     */
    /*private Observer<IMessage> messageStatusObserver = new Observer<IMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (isMyMessage(message)) {
                onMessageStatusChange(message);
            }
        }
    };*/

    /**
     * 消息附件上传/下载进度观察者
     */
    /*private Observer<AttachmentProgress> attachmentProgressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress progress) {
            onAttachmentProgressChange(progress);
        }
    };*/

    /**
     * 消息撤回观察者
     */
  /*  private Observer<RevokeMsgNotification> revokeMessageObserver = new Observer<RevokeMsgNotification>() {
        @Override
        public void onEvent(RevokeMsgNotification notification) {
            if (notification == null || notification.getMessage() == null) {
                return;
            }
            IMMessage message = notification.getMessage();
            // 获取通知类型： 1表示是离线，2表示是漫游 ，默认 0
            Log.i(TAG, "notification type = " + notification.getNotificationType());

            if (!container.account.equals(message.getSessionId())) {
                return;
            }

            deleteItem(message, false);
        }
    };
*/
    /**
     * 群消息已读回执观察者
     */
   /* private Observer<List<TeamMessageReceipt>> teamMessageReceiptObserver = new Observer<List<TeamMessageReceipt>>() {
        @Override
        public void onEvent(List<TeamMessageReceipt> teamMessageReceipts) {
            for (TeamMessageReceipt teamMessageReceipt : teamMessageReceipts) {
                int index = getItemIndex(teamMessageReceipt.getMsgId());
                if (index >= 0 && index < items.size()) {
                    refreshViewHolderByIndex(index);
                }
            }
        }
    };*/
    /**
     * 用户信息观察者
     */
    /*private UserInfoObserver userInfoObserver = new UserInfoObserver() {
        @Override
        public void onUserInfoChanged(List<String> accounts) {
            if (container.sessionType == SessionTypeEnum.P2P) {
                if (accounts.contains(container.account) || accounts.contains(NimUIKit.getAccount())) {
                    adapter.notifyDataSetChanged();
                }
            } else { // 群的，简单的全部重刷
                adapter.notifyDataSetChanged();
            }
        }
    };*/
    /**
     * 本地消息接收观察者
     */
   /* private MessageListPanelHelper.LocalMessageObserver incomingLocalMessageObserver = new MessageListPanelHelper.LocalMessageObserver() {
        @Override
        public void onAddMessage(IMMessage message) {
            if (message == null || !container.account.equals(message.getSessionId())) {
                return;
            }
            onMsgSend(message);
        }

        @Override
        public void onClearMessages(String account) {
            items.clear();
//            refreshMessageList();
            adapter.notifyDataSetChanged();
            adapter.fetchMoreEnd(null, true);
        }
    };*/

    /**
     * ************************* 观察者 ********************************
     */
    private void registerObservers(boolean register) {
       /* MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeMsgStatus(messageStatusObserver, register);
        service.observeAttachmentProgress(attachmentProgressObserver, register);
        service.observeRevokeMessage(revokeMessageObserver, register);
        service.observeTeamMessageReceipt(teamMessageReceiptObserver, register);

        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register);
        MessageListPanelHelper.getInstance().registerObserver(incomingLocalMessageObserver, register);*/
    }


    private void onMessageStatusChange(IMessage message) {
        int index = getItemIndex(message.getUuid());
        if (index >= 0 && index < items.size()) {
//            IMMessage item = items.get(index);
//            item.setStatus(message.getStatus());
//            item.setAttachStatus(message.getAttachStatus());
//            // 处理语音、音视频通话
//            if (item.getMsgType() == MsgTypeEnum.audio || item.getMsgType() == MsgTypeEnum.avchat) {
//                item.setAttachment(message.getAttachment()); // 附件可能更新了
//            }
//
            items.set(index, message);
//            // resend的的情况，可能时间已经变化了，这里要重新检查是否要显示时间
            List<IMessage> msgList = new ArrayList<>(1);
            msgList.add(message);
            adapter.updateShowTimeItem(msgList, false, true);
            refreshViewHolderByIndex(index);
        }
    }

    /*private void onAttachmentProgressChange(AttachmentProgress progress) {
        int index = getItemIndex(progress.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            float value = (float) progress.getTransferred() / (float) progress.getTotal();
            adapter.putProgress(item, value);
            refreshViewHolderByIndex(index);
        }
    }*/

    private boolean isMyMessage(IMessage message) {
        return message.getSessionType() == container.session.getSessionType()
                && message.getSessionId() != null
                && message.getSessionId().equals(container.session.getSessionId());
    }

    /**
     * 刷新单条消息
     */
    private void refreshViewHolderByIndex(final int index) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (index < 0) {
                    return;
                }
                adapter.notifyDataItemChanged(index);
            }
        });
    }

    private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            IMessage message = items.get(i);
            if (TextUtils.equals(message.getUuid(), uuid)) {
                return i;
            }
        }
        return -1;
    }

    public void setChattingBackground(String uriString, int color) {
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            if (uri.getScheme().equalsIgnoreCase("file") && uri.getPath() != null) {
                ivBackground.setImageBitmap(getBackground(uri.getPath()));
            } else if (uri.getScheme().equalsIgnoreCase("android.resource")) {
                List<String> paths = uri.getPathSegments();
                if (paths == null || paths.size() != 2) {
                    return;
                }
                String type = paths.get(0);
                String name = paths.get(1);
                String pkg = uri.getHost();
                int resId = container.activity.getResources().getIdentifier(name, type, pkg);
                if (resId != 0) {
                    ivBackground.setBackgroundResource(resId);
                }
            }
        } else if (color != 0) {
            ivBackground.setBackgroundColor(color);
        }
    }

    /**
     * ***************************************** 数据加载 *********************************************
     */



    private void setEarPhoneMode(boolean earPhoneMode, boolean update) {
      /*  if (update) {
            UserPreferences.setEarPhoneModeEnable(earPhoneMode);
        }
        MessageAudioControl.getInstance(container.activity).setEarPhoneModeEnable(earPhoneMode);*/
    }

    private Bitmap getBackground(String path) {
        if (background != null && path.equals(background.first) && background.second != null) {
            return background.second;
        }

        if (background != null && background.second != null) {
            background.second.recycle();
        }

        Bitmap bitmap = null;
        if (path.startsWith("/android_asset")) {
            String asset = path.substring(path.indexOf("/", 1) + 1);
            try {
                InputStream ais = container.activity.getAssets().open(asset);
                bitmap = BitmapDecoder.decodeSampled(ais, ScreenUtil.screenWidth, ScreenUtil.screenHeight);
                ais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bitmap = BitmapDecoder.decodeSampled(path, ScreenUtil.screenWidth, ScreenUtil.screenHeight);
        }
        background = new Pair<>(path, bitmap);
        return bitmap;
    }


    /**
     * 收到已读回执（更新VH的已读label）
     */
    public void receiveReceipt() {
        updateReceipt(items);
        refreshMessageList();
    }

    private void updateReceipt(final List<IMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (receiveReceiptCheck(messages.get(i))) {
                adapter.setUuid(messages.get(i).getUuid());
                break;
            }
        }
    }

    private boolean receiveReceiptCheck(final IMessage msg) {
        return msg != null
                && msg.getSessionType() == SessionType.P2P
                && msg.getDirect() == MsgDirection.Out
            /*    && msg.getMsgType() != MsgTypeEnum.tip
                && msg.getMsgType() != MsgTypeEnum.notification*/
                && msg.isRemoteRead();

    }

    /**
     * 发送已读回执（需要过滤）
     */

    public void sendReceipt() {
        // 查询全局已读回执功能开关配置
       /* if (!NimUIKitImpl.getOptions().shouldHandleReceipt) {
            return;
        }*/

        if (container.session.getSessionId() == null || container.session.getSessionType() != SessionType.P2P) {
            return;
        }

        IMessage message = getLastReceivedMessage();
        if (!sendReceiptCheck(message)) {
            return;
        }

      //  NIMClient.getService(MsgService.class).sendMessageReceipt(container.account, message);
    }

    private IMessage getLastReceivedMessage() {
        IMessage lastMessage = null;
        for (int i = items.size() - 1; i >= 0; i--) {
            if (sendReceiptCheck(items.get(i))) {
                lastMessage = items.get(i);
                break;
            }
        }

        return lastMessage;
    }

    private boolean sendReceiptCheck(final IMessage msg) {
        return msg != null
                && msg.getDirect() == MsgDirection.In
           /*     && msg.getMsgType() != MsgTypeEnum.tip
                && msg.getMsgType() != MsgTypeEnum.notification*/;
    }

    // 删除消息
    private void deleteItem(IMessage messageItem, boolean isRelocateTime) {
       // NIMClient.getService(MsgService.class).deleteChattingHistory(messageItem);
        List<IMessage> messages = new ArrayList<>();
        for (IMessage message : items) {
            if (message.getUuid().equals(messageItem.getUuid())) {
                continue;
            }
            messages.add(message);
        }
        updateReceipt(messages);
        adapter.deleteItem(messageItem, isRelocateTime);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        /*final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
        if (CommonUtil.isEmpty(selected)) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_FORWARD_TEAM:
                doForwardMessage(selected.get(0), SessionTypeEnum.Team);
                break;
            case REQUEST_CODE_FORWARD_PERSON:
                doForwardMessage(selected.get(0), SessionTypeEnum.P2P);
                break;
        }*/

    }

    // 转发消息
   /* private void doForwardMessage(final String sessionId, SessionTypeEnum sessionTypeEnum) {
        IMMessage message;
        if (forwardMessage.getMsgType() == MsgTypeEnum.robot) {
            message = buildForwardRobotMessage(sessionId, sessionTypeEnum);
        } else {
            message = MessageBuilder.createForwardMessage(forwardMessage, sessionId, sessionTypeEnum);
        }
        if (message == null) {
            ToastHelper.showToast(container.activity, "该类型不支持转发");
            return;
        }
        if (container.proxySend) {
            container.proxy.sendMessage(message);
        } else {
            NIMClient.getService(MsgService.class).sendMessage(message, false);
            if (container.account.equals(sessionId)) {
                onMsgSend(message);
            }
        }

    }*/

   /* private IMMessage buildForwardRobotMessage(final String sessionId, SessionTypeEnum sessionTypeEnum) {
        if (forwardMessage.getMsgType() == MsgTypeEnum.robot && forwardMessage.getAttachment() != null) {
            RobotAttachment robotAttachment = (RobotAttachment) forwardMessage.getAttachment();
            if (robotAttachment.isRobotSend()) {
                return null; // 机器人发的消息不能转发了
            }
            return MessageBuilder.createTextMessage(sessionId, sessionTypeEnum, forwardMessage.getContent());
        }

        return null;
    }*/
}
