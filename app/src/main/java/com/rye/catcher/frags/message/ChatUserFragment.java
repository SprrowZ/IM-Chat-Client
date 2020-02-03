package com.rye.catcher.frags.message;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.rye.catcher.R;
import com.rye.catcher.activities.PersonalActivity;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.factory.model.db.User;
import com.rye.factory.persenter.message.ChatContract;
import com.rye.factory.persenter.message.ChatUserPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment extends ChatFragment<User> implements ChatContract.UserView {
   @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    private MenuItem mUserInfoMenuItem;
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        RequestOptions options=new RequestOptions();
        options.centerCrop();
        // TODO: 2020/1/28 待替换，待了解---渐变效果
        Glide.with(this)
                .load(R.drawable.default_banner_chat)
                .apply(options)
                .into(new ViewTarget<CollapsingToolbarLayout,Drawable>(mCollapsingBarLayout) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        Toolbar toolbar=mToolbar;
        toolbar.inflateMenu(R.menu.chat_user);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId()==R.id.action_person){
                onPortraitClick();
            }
            return false;
        });
        //拿到菜单Icon
        mUserInfoMenuItem=toolbar.getMenu().findItem(R.id.action_person);

    }
    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        PersonalActivity.show(getContext(),mReceiverId);
    }
    //进行高度的综合运算，透明我们的头像和ICON
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view=mPortrait;

        MenuItem menuItem=mUserInfoMenuItem;
        if (view==null  ||menuItem==null) return;

        if (verticalOffset==0){//完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
            //将右侧个人头像隐藏
            menuItem.setVisible(false);
            menuItem.getIcon().setAlpha(0);

        }else{
            verticalOffset=Math.abs(verticalOffset);//取正
            //滚动的最高高度
            final int totalScroll=appBarLayout.getTotalScrollRange();
            if (verticalOffset>=totalScroll){
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

                //显示菜单
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255);
            }else{//中间状态
                float progress=1-verticalOffset/(float)totalScroll;
                Log.i("ChatUserFragment",progress+"");
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);

                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255-(int)(255*progress));//和中间个人头像透明度相反
            }
        }
    }


    @Override
    protected ChatContract.Presenter initPresenter() {
        //初始化Presenter
        return new ChatUserPresenter(this,mReceiverId);
    }

    @Override
    public void onInit(User user) {
       //当前聊天对象的初始化
        mPortrait.setUp(Glide.with(this),user.getPortrait());
        mCollapsingBarLayout.setTitle(user.getName());
    }
}
