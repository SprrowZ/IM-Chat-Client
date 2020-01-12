package com.rye.catcher;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import com.rye.catcher.activities.AccountActivity;
import com.rye.catcher.activities.MainActivity;
import com.rye.catcher.frags.assist.PermissionsFragment;
import com.rye.catcher.common.app.BaseActivity;
import com.rye.factory.persistence.Account;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

// TODO: 2020/1/11 Launch待改造----有点子low-- 
public class LaunchActivity extends BaseActivity {

    private ColorDrawable mBgDrawable;
    // 是否已经得到PushId
    private boolean mAlreadyGotPushReceiverId = false;
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        View root = findViewById(R.id.activity_launch);
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        ColorDrawable drawable = new ColorDrawable(color);
        //设置背景色
        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    @Override
    protected void initData() {
        super.initData();
        //开启动画,执行到百分之五十等待pushId
        // 动画进入到50%等待PushId获取到
        // 检查等待状态
        startAnim(0.5f, this::waitPushReceiverId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 判断是否已经得到推送Id，如果已经得到则进行跳转操作，
        // 在操作中检测权限状态
        if (mAlreadyGotPushReceiverId) {
            reallySkip();
        }
    }
    /**
     * 在跳转之前需要把剩下的50%进行完成
     */
    private void waitPushReceiverIdDone() {
        // 标志已经得到PushId
        mAlreadyGotPushReceiverId = true;
        startAnim(1f, this::reallySkip);
    }
    /**
     * 等待个推sdk对我们的pushId进行设置
     */
    private void waitPushReceiverId() {
        //判断是否登录
        if (Account.isLogined()) {
            //判断是否绑定设备，如果没有，就得我们MessageReceiver进行绑定
            if (Account.isBind()) {
                waitPushReceiverIdDone();
                return;
            }
        } else {
            //没有登录但已经绑定过设备ID了
            if (!TextUtils.isEmpty(Account.getPushId())) {
                //跳转
                waitPushReceiverIdDone();
                return;
            }
        }
        // TODO: 2020/1/11 感觉得设置一个最大尝试次数，这样无限循环放到真实项目中，必定要被打死
        //间隔500ms，循环等待
        // 循环等待
        getWindow().getDecorView()
                .postDelayed(this::waitPushReceiverId, 500);
    }


    /**
     * 真正的跳转操作
     */
    private void reallySkip() {
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {

            if (Account.isLogined()) {//已经登录的情况下跳转到主页
                MainActivity.show(this);
            } else {//否则跳转到账户界面去注册登陆
                AccountActivity.show(this);
            }
            finish();
        }
    }

    /**
     * 给背景设置一个渐变的动画
     *
     * @param endProgress
     * @param endCallback
     */
    private void startAnim(float endProgress, final Runnable endCallback) {
        int finalColor = Resource.Color.WHITE;
        //运算当前进度的颜色--因为颜色值是渐变的
        // TODO: 2020/1/11 待了解ArgbEvaluator以及和属性动画的搭配使用
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        //构建一个属性动画，这个传入Property的倒是第一次见。。
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(1500);
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor);
        //有两个监听器，这里别用错了
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    /**
     * 设置渐变的属性ORZ
     */
    private Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }

        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }
    };


}
