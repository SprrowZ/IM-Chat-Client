package com.rye.catcher.common.widget;


import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.rye.catcher.factory.model.Author;
import com.rye.common.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created at 2018/10/17.
 *个人头像
 * @author Zzg
 */
public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setUp(RequestManager manager, Author author){
        if (author==null||manager==null){
            return;
        }
        setUp(manager,author.getPortrait());
    }
    public void setUp(RequestManager manager,String url){
        if (manager==null || url==null){
            return;
        }
        setUp(manager,url, R.drawable.default_portrait);
    }

    public void setUp(RequestManager manager,String url,int resouceId){
        if (url==null){
            url="";
        }
        RequestOptions options=new RequestOptions();
        options.centerCrop();
        options.placeholder(resouceId);
        options.dontAnimate();//不使用动画，否则加载会缓慢
        manager.load(url).into(this);
    }
}
