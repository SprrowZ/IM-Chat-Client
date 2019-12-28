package com.rye.catcher.activities;



import android.content.Context;
import android.content.Intent;

import com.rye.catcher.R;
import com.rye.catcher.frags.account.UpdateInfoFragment;
import com.rye.catcher.common.app.BaseActivity;

public class AccountActivity extends BaseActivity {
    private UpdateInfoFragment mCurFragment=new UpdateInfoFragment();
    /**
     * 账户activity的入口
     * 跳转到本Activity的封装方法，感觉很机智
     * @param context
     */
    public static void show(Context context){
        context.startActivity(new Intent(context,AccountActivity.class));
    }




    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
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
