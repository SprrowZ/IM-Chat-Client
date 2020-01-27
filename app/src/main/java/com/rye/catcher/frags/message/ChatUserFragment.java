package com.rye.catcher.frags.message;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.rye.catcher.R;
import com.rye.catcher.activities.PersonalActivity;
import com.rye.catcher.common.widget.PortraitView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment extends ChatFragment {
   @BindView(R.id.im_portrait)
    PortraitView mPortrait;
   private MenuItem mUserInfoMenuItem;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_user;
    }
    @Override
    protected void initToolbar() {
        super.initToolbar();
        Toolbar toolbar=mToolbar;
        toolbar.inflateMenu(R.menu.chat_user);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.action_person){
                    onPortraitClick();
                }
                return false;
            }
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


}
