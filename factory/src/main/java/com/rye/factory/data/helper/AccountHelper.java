package com.rye.factory.data.helper;


import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.account.AccountRspModel;
import com.rye.factory.model.api.account.RegisterModel;
import com.rye.factory.model.db.User;
import com.rye.factory.net.NetWork;
import com.rye.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 * 账户相关的网络逻辑处理
 */
public class AccountHelper {


    /**
     * 注册的接口
     *
     * @param model    传入一个注册Model进来
     * @param callback
     */
    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {
        RemoteService remoteService = NetWork.getRetrofit().create(RemoteService.class);
        Call<RspModel<AccountRspModel>> call = remoteService.accountRegister(model);
        call.enqueue(new Callback<RspModel<AccountRspModel>>() {
            @Override
            public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
                //请求成功
                RspModel<AccountRspModel> rspModel = response.body();
                if (rspModel.success()) {
                    AccountRspModel accountRspModel = rspModel.getResult();
                    //判断绑定设备
                    if (accountRspModel.isBind()) {
                        User user = accountRspModel.getUser();
                        //存储到数据库中
                        //回调给上层
                        callback.onDataLoaded(user);
                    } else {
                        bindPush(callback);
                    }
                } else {
                    //对返回失败的提示
                    Factory.decodeResource(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
                //请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 对设备Id进行绑定的操作
     * @param callback
     */
   public static void bindPush(final DataSource.Callback<User> callback){
       // TODO: 2020/1/7 先抛出错误，实际上是我们的绑定操作，暂时处理
       callback.onDataNotAvailable(R.string.data_network_error);
   }
}
