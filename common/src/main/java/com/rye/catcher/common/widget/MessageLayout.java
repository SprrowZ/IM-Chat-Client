package com.rye.catcher.common.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import net.qiujuer.widget.airpanel.AirPanelLinearLayout;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 * 解决聊天界面顶部浸入与底部聊天框的冲突问题，
 * 可不用覆写，
 */
// TODO: 2020/1/27 可以不用覆写
public class MessageLayout extends AirPanelLinearLayout {

    public MessageLayout(Context context) {
        super(context);
    }

    public MessageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            insets.left=0;
            insets.top=0;
            insets.right=0;
        }
        
        return super.fitSystemWindows(insets);
        
    }
}
