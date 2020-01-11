package com.rye.catcher.activities;

import android.content.Intent;

import com.rye.catcher.R;
import com.rye.catcher.frags.user.UpdateInfoFragment;
import com.rye.catcher.common.app.BaseActivity;

public class UserActivity extends BaseActivity {
    private UpdateInfoFragment mCurFragment=new UpdateInfoFragment();
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }
    @Override
    protected void initWidget() {
        super.initWidget();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_dialog_anim,0)//添加进场动画
                .add(R.id.lay_container,mCurFragment)
                .commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
