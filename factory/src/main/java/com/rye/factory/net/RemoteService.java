package com.rye.factory.net;

import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.account.AccountRspModel;
import com.rye.factory.model.api.account.LoginModel;
import com.rye.factory.model.api.account.RegisterModel;
import com.rye.factory.model.api.user.UserUpdateModel;
import com.rye.factory.model.card.UserCard;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * CreateBy ShuQin
 * at 2020/1/6
 * q全局网络请求接口
 */
public interface RemoteService {
    /**
     * 注册接口
     * @param model
     * @return
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登陆接口
     * @param model
     * @return
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备ID
     * @param pushId
     * @return
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true,value = "pushId") String pushId);

    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);



}
