package com.rye.factory.data.helper;

import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.account.AccountRspModel;
import com.rye.factory.model.api.user.UserUpdateModel;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.User;
import com.rye.factory.net.NetWork;
import com.rye.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CreateBy ShuQin
 * at 2020/1/12
 */
public class UserHelper {
    //更新账户信息，异步
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        final RemoteService remoteService = NetWork.remote();
        Call<RspModel<UserCard>> call = remoteService.userUpdate(model);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    //数据库的存储操作，需要把UserCard转换为User
                    UserCard userCard = rspModel.getResult();
                    User user = userCard.build();
                    user.save();//保存到数据库中
                    callback.onDataLoaded(userCard);
                } else {
                    //错误情况处理
                    Factory.decodeResource(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }
}
