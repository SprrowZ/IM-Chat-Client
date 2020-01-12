package com.rye.factory.data.helper;


import android.text.TextUtils;

import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.account.AccountRspModel;
import com.rye.factory.model.api.account.LoginModel;
import com.rye.factory.model.api.account.RegisterModel;
import com.rye.factory.model.db.User;
import com.rye.factory.net.NetWork;
import com.rye.factory.net.RemoteService;
import com.rye.factory.persistence.Account;

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
        RemoteService remoteService = NetWork.remote();
        Call<RspModel<AccountRspModel>> call = remoteService.accountRegister(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 登陆的请求
     * @param model
     * @param callback
     */
    public static void Login(LoginModel model, final DataSource.Callback<User> callback) {
        RemoteService remoteService = NetWork.remote();
        Call<RspModel<AccountRspModel>> call = remoteService.accountLogin(model);
        call.enqueue(new AccountRspCallback(callback));
    }
    /**
     * 对设备Id进行绑定的操作
     * @param callback
     */
   public static void bindPush(final DataSource.Callback<User> callback){
       // TODO: 2020/1/7 先抛出错误，实际上是我们的绑定操作，暂时处理
       String pushId=Account.getPushId();
       if (TextUtils.isEmpty(pushId))
           return;
//       Account.setBind(true);
       RemoteService service=NetWork.remote();
       Call<RspModel<AccountRspModel>> call=service.accountBind(pushId);
       //emmm....合着注册和bind的处理是一样的？？？？
       call.enqueue(new AccountRspCallback(callback));

   }

    /**
     * 这里处理了登陆、注册、bind的所有业务操作
     * 所以共用了这一个AccountRspCallback
     */
   private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>>{
       final  DataSource.Callback<User> callback;
       public  AccountRspCallback(DataSource.Callback<User> callback){
           this.callback=callback;
       }
       @Override
       public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
           //请求成功
           RspModel<AccountRspModel> rspModel = response.body();
           if (rspModel.success()) {
               AccountRspModel accountRspModel = rspModel.getResult();
               User user = accountRspModel.getUser();
               user.save();
               // TODO: 2020/1/11 待了解所有的DBFlow存储方法
               //同步到sp中
               Account.login(accountRspModel);
               //判断绑定设备
               if (accountRspModel.isBind()) {
                   //十分关键，不要忘了这一步
                   Account.setBind(true);
                   //回调给上层
                   if (callback!=null)
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
           if (callback!=null)
           callback.onDataNotAvailable(R.string.data_network_error);
       }
   }





    //DBFlow第二种方法
//                        FlowManager.getModelAdapter(User.class).login(user);
//                        //第三种方法--开启事务
//                        DatabaseDefinition definition=FlowManager.getDatabase(AppDatabase.class);
//                        definition.beginTransactionAsync(new ITransaction() {
//                            @Override
//                            public void execute(DatabaseWrapper databaseWrapper) {
//                                FlowManager.getModelAdapter(User.class).login(user);
//                            }
//                        }).build().execute();
}
