package com.rye.catcher;

import android.app.Activity;
import android.os.Bundle;

import com.igexin.sdk.PushManager;
import com.rye.catcher.common.app.zApplication;
import com.rye.factory.Factory;

/**
 * Created at 2018/11/7.
 *
 * @author Zzg
 */
public class App extends zApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化设备ID
        Factory.setup();
        //初始化个推----推送初始化
        // 注册生命周期
        registerActivityLifecycleCallbacks(new PushInitializeLifecycle());
    }

    private class PushInitializeLifecycle implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            // 推送进行初始化
            PushManager.getInstance().initialize(App.this, AppPushService.class);
            // 推送注册消息接收服务
            PushManager.getInstance().registerPushIntentService(App.this, AppMessageReceiverService.class);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
