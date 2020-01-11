package com.rye.factory;

import android.app.Application;

import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.model.api.RspModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CreateBy ShuQin
 * at 2019/12/29
 */
public class Factory {
   private static final Factory instance;
    //全局线程池
    private final Executor executor;

    private final Gson gson;

    static {
        instance=new Factory();
    }

    private Factory(){
        executor= Executors.newFixedThreadPool(4);

        gson=new GsonBuilder()
                //时间格式和服务器保持一直
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                // TODO: 2020/1/6 设置过滤级别
                .setExclusionStrategies()
                .create();
    }

    /**
     * 返回全局的Application
     * @return
     */
    public static Application app(){
        return zApplication.getInstance();
    }

    /**
     * 异步运行的方法
     * @param runnable
     */
    public static void runOnAsync(Runnable runnable){
        //拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

    /**
     * 返回一个全局的Gson，进行全局的Gson初始化
     * @return
     */
    public  static  Gson getGson(){
        return instance.gson;
    }


    /**
     * 解析错误Code，把服务端自定义的错误Code解析成我们对应的String
     * @param model
     * @param callback
     */
     public static void decodeResource(RspModel model , DataSource.FailedCallback callback){
           if(model==null)
               return;
           switch (model.getCode()){
               case RspModel.SUCCEED:
                   return;
               case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service,callback);
                break;
               case RspModel.ERROR_NOT_FOUND_USER:
                   decodeRspCode(R.string.data_rsp_error_not_found_user,callback);
                   break;
               case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                   decodeRspCode(R.string.data_rsp_error_not_found_group_member,callback);
                   break;
               case RspModel.ERROR_CREATE_USER:
                   decodeRspCode(R.string.data_rsp_error_create_user,callback);
                   break;
               case RspModel.ERROR_CREATE_GROUP:
                   decodeRspCode(R.string.data_rsp_error_create_group,callback);
                   break;
               case RspModel.ERROR_CREATE_MESSAGE:
                   decodeRspCode(R.string.data_rsp_error_create_message,callback);
                   break;
               case RspModel.ERROR_PARAMETERS:
                   decodeRspCode(R.string.data_rsp_error_parameters,callback);
                   break;
               case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                   decodeRspCode(R.string.data_rsp_error_parameters_exist_account,callback);
                   break;
               case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                   decodeRspCode(R.string.data_rsp_error_parameters_exist_name,callback);
                   break;
               case RspModel.ERROR_ACCOUNT_TOKEN:
                   zApplication.showToast(R.string.data_rsp_error_account_token);
                   instance.logout();
                   break;
               case RspModel.ERROR_ACCOUNT_LOGIN:
                   decodeRspCode(R.string.data_rsp_error_account_login,callback);
                   break;
               case RspModel.ERROR_ACCOUNT_REGISTER:
                   decodeRspCode(R.string.data_rsp_error_account_register,callback);
                   break;
               case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                   decodeRspCode(R.string.data_rsp_error_account_no_permission,callback);
                   break;
               case RspModel.ERROR_UNKNOWN:
                   decodeRspCode(R.string.data_rsp_error_unknown,callback);
                   break;

           }
     }

     private static void decodeRspCode(@StringRes final int resId,
                                       final DataSource.FailedCallback callback){
         if (callback!=null){
             callback.onDataNotAvailable(resId);
         }
     }

    /**
     * 收到账户退出的信息需要退出账户重新登陆
     */
    private void logout(){

     }
}
