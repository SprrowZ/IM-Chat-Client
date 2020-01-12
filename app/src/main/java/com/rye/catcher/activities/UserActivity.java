package com.rye.catcher.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rye.catcher.R;
import com.rye.catcher.frags.user.UpdateInfoFragment;
import com.rye.catcher.common.app.BaseActivity;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

public class UserActivity extends BaseActivity {
    @BindView(R.id.im_bg)
    ImageView mBg;

    private UpdateInfoFragment mCurFragment=new UpdateInfoFragment();

    public static void show(Context context){
        context.startActivity(new Intent(context,UserActivity.class));
    }



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


        //初始化背景
        RequestOptions options=new RequestOptions();
        options.centerCrop();
        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        //拿到当前背景
                        Drawable drawable=resource.getCurrent();
                        drawable= DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(),R.color.colorAccent),
                                PorterDuff.Mode.SCREEN);
                        mBg.setImageDrawable(drawable);
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
