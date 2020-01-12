package com.rye.catcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.rye.factory.Factory;
import com.rye.factory.data.helper.AccountHelper;
import com.rye.factory.persistence.Account;

/**
 * CreateBy ShuQin
 * at 2020/1/11
 */
public class MessageReceiver extends BroadcastReceiver {

    private static final String TAG = "MessageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        Bundle bundle = intent.getExtras();
        //判断当前消息的意图
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:
                //获取设备Id
                Log.i(TAG,"GET_CLIENTID:"+bundle.toString());
                onClientInit(bundle.getString("clientId"));
                break;
            case PushConsts.GET_MSG_DATA: {
                 //接收到消息的时候
                byte[] payLoad = bundle.getByteArray("payload");
                if (payLoad != null) {
                    String message = new String(payLoad);
                    Log.i(TAG, "GET_MSG_DATA:" + message);
                    onMessageArrived(message);
                    break;
                }
            }
            default:
                Log.i(TAG, "Other:" + bundle.toString());
                break;


        }

    }

    /**
     * 设备ID初始化
     * @param cid
     */
    private void onClientInit(String cid) {
        Account.setPushId(cid);
        if (Account.isLogined()){
            //账户在登陆的情况下，绑定pushId;
            //没有登录的情况下，是不能进行绑定的，原因你懂的
            AccountHelper.bindPush(null);
        }
    }

    private void onMessageArrived(String message) {
        //消息交给Factory处理
        Factory.disPatchPush(message);
    }


}
