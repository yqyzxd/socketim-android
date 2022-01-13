package com.wind.im.ui.viewholder;

import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.wind.im.IMContext;
import com.wind.im.R;
import com.wind.im.emoji.MoonUtil;
import com.wind.im.ui.BaseMultiItemFetchLoadAdapter;
import com.wind.im.util.ScreenUtil;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderText extends MsgViewHolderBase {

    protected TextView bodyTextView;

    public MsgViewHolderText(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_text;
    }

    @Override
    protected void inflateContentView() {
        bodyTextView = findViewById(R.id.nim_message_item_text_body);
    }

    @Override
    protected void bindContentView() {
        layoutDirection();
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });

        MoonUtil.identifyFaceExpression(context, bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    private void layoutDirection() {
        if (isReceivedMessage()) {
            bodyTextView.setBackgroundResource(IMContext.getOptions().messageLeftBackground);
            bodyTextView.setTextColor(Color.parseColor("#0d0d0d"));
            bodyTextView.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
        } else {
            bodyTextView.setBackgroundResource(IMContext.getOptions().messageRightBackground);
            bodyTextView.setTextColor(Color.parseColor("#0d0d0d"));
            bodyTextView.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
        }
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return message.getContent();
    }


    @Override
    protected boolean shouldDisplayReceipt() {
        return true;
    }
}
