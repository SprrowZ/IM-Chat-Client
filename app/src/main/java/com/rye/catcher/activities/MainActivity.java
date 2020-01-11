package com.rye.catcher.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rye.catcher.R;
import com.rye.catcher.frags.main.ActiveFragment;
import com.rye.catcher.frags.main.ContactFragment;
import com.rye.catcher.frags.main.GroupFragment;
import com.rye.catcher.help.NavHelper;
import com.rye.catcher.common.app.BaseActivity;
import com.rye.catcher.common.widget.PortraitView;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
 NavHelper.OnTabChangedListener<Integer>{
    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private  NavHelper mNavHelper;

    /**
     * MainActivity入口
     * @param context
     */
   public static void show(Context context){
       context.startActivity(new Intent(context,MainActivity.class));
   }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //申请权限
//        PermissionUtils.requestPermission(this,"需要存储权限",action->{
//            Log.i("MainActivity  ", "initWidget: Permission Success...");
//
//        }, Permission.Group.STORAGE);
        //初始化底部辅助工具类
        mNavHelper=new NavHelper(this,R.id.lay_container,
                getSupportFragmentManager(),this);
        //将Fragment加进来，同时也传入对应的Title
        mNavHelper.add(R.id.action_home,new NavHelper.Tab<>(ActiveFragment.class,R.string.title_home))
                .add(R.id.action_group,new NavHelper.Tab<>(GroupFragment.class,R.string.title_group))
                .add(R.id.action_contact,new NavHelper.Tab<>(ContactFragment.class,R.string.title_contact));


        mNavigation.setOnNavigationItemSelectedListener(this);
        //顶部背景图片
        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Log.i("zzg", "onResourceReady: ");
                        mLayAppbar.setBackground(resource.getCurrent());
                    }
                });


    }

    @Override
    protected void initData() {
        super.initData();
        //初次进入的时候加载第一个Fragment，从底部接管我们的Menu
        Menu menu=mNavigation.getMenu();
        menu.performIdentifierAction(R.id.action_home,0);//然后触发底下的方法，接着就是一连串把title也替换了
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick(){

    }
    @OnClick(R.id.btn_action)
    void onActionClick(){
        AccountActivity.show(this);
    }


    /**
     * 当底部按钮点击时触发
     * @param item
     * @return True代表我们可以点击事件
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        return mNavHelper.performClickMenu(item.getItemId());
}

    /**
     * 切换底部的操作
     * @param newTab
     * @param oldTab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
       mTitle.setText(newTab.extra);
               //实现浮动按钮动画
        float tranY=0;
        float rotation=0;
        if (Objects.equals(newTab.extra,R.string.title_home)){
            tranY= (int) Ui.dipToPx(getResources(),76);
        }else if (Objects.equals(newTab.extra,R.string.title_group)){
          rotation=-360;

        }else {
          rotation=360;
        }
        mAction.animate()
                .translationY(tranY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .rotation(rotation)
                .setDuration(480)
                .start();
    }
}
