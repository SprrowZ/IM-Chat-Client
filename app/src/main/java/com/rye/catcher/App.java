package com.rye.catcher;

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
        PushManager.getInstance().initialize(this);
    }
}
