package com.wind.im.ui.widget;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created By shao
 * on 2020-03-03
 * <p>
 * 描述:
 */
public class AutoLinkTextView  extends TextView {
    public AutoLinkTextView(Context context) {
        super(context);
        setAutoLinkMask(Linkify.WEB_URLS);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAutoLinkMask(Linkify.WEB_URLS);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAutoLinkMask(Linkify.WEB_URLS);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        replace();
    }

    @Override
    public void append(CharSequence text, int start, int end) {
        super.append(text, start, end);
        replace();
    }

    private void replace() {
        CharSequence text = getText();

        if (text instanceof SpannableString) {
            SpannableString spannableString = (SpannableString) text;
            Class<? extends SpannableString> aClass = spannableString.getClass();

            try {
                //mSpans属性属于SpannableString的父类成员
                Class<?> aClassSuperclass = aClass.getSuperclass();
                Field mSpans = aClassSuperclass.getDeclaredField("mSpans");
                mSpans.setAccessible(true);
                Object o = mSpans.get(spannableString);

                if (o.getClass().isArray()) {
                    Object objs[] = (Object[]) o;

                    if (objs.length > 1) {
                        //这里的第0个位置不稳妥，实际环境可能会有多个链接地址
                        Object obj = objs[0];
                        if (obj.getClass().equals(URLSpan.class)) {

                            //获取URLSpan的mURL值，用于新的URLSpan的生成
                            Field oldUrlField = obj.getClass().getDeclaredField("mURL");
                            oldUrlField.setAccessible(true);
                            Object o1 = oldUrlField.get(obj);

                            //生成新的自定义的URLSpan，这里我们将这个自定义URLSpan命名为ExtendUrlSpan
                            Constructor<?> constructor = AutoLinkTextView.ExtendUrlSpan.class.getConstructor(String.class);
                            constructor.setAccessible(true);
                            Object newUrlField = constructor.newInstance(o1.toString());

                            //替换
                            objs[0] = newUrlField;
                        }
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ExtendUrlSpan extends URLSpan {
        public ExtendUrlSpan(String url) {
            super(url);
        }

        public ExtendUrlSpan(Parcel src) {
            super(src);
        }

        @Override
        public void onClick(View widget) {
            //这个方法会在点击链接的时候调用，可以实现自定义事件
//            Toast.makeText(widget.getContext(), getURL(), Toast.LENGTH_SHORT).show();
            if (!getURL().contains("51marryyou")) {
                String uidJson = getUrlJson(getURL());
                ARouter.getInstance().build("/msg/activity_jump_toast")
                        .withString("extra_key_json", uidJson)
                        .navigation();
            } else {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(getURL());
                intent.setData(content_url);
                try {
                    widget.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //ToastHelper.showToast( widget.getContext(), "路径错误");
                }
            }





        }


        public static String getUrlJson(String url) {

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{")
                    .append("\"")
                    .append("url")
                    .append("\"")
                    .append(":")
                    .append("\"")
                    .append(url)
                    .append("\"")
                    .append("}");
            return jsonBuilder.toString();

        }
    }


}