package com.rye.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.user.UserUpdateModel;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.User;
import com.rye.factory.model.db.view.UserSampleModel;
import com.rye.factory.model.db.User_Table;
import com.rye.factory.net.NetWork;
import com.rye.factory.net.RemoteService;
import com.rye.factory.persistence.Account;

import java.io.IOException;
import java.util.List;

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
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    //错误情况处理
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 搜索用户
     *
     * @param name
     * @param callback
     * @return 因为用户可能有重复点击的行为，必须有取消的动作
     */
    public static Call search(final String name, final DataSource.Callback<List<UserCard>> callback) {
        final RemoteService remoteService = NetWork.remote();
        Call<RspModel<List<UserCard>>> call = remoteService.userSearch(name);
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }

    /**
     * 关注某人
     *
     * @param id
     * @param callback
     */
    public static void follow(final String id, final DataSource.Callback<UserCard> callback) {
        final RemoteService remote = NetWork.remote();
        Call<RspModel<UserCard>> call = remote.userFollow(id);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    //关注后就是我的好友了，需要保存到本地
                    UserCard userCard = rspModel.getResult();
                    //保存
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 刷新联系人
     * 不需要callback，直接通过数据库的观察者模式反馈给上层
     * 界面更新的时候进行对比，然后差异更新
     */
    public static void refreshContacts( ) {
        final RemoteService remote = NetWork.remote();
        Call<RspModel<List<UserCard>>> call = remote.userContacts();
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<UserCard> cards=rspModel.getResult();
                    if (cards==null || cards.size()==0) return;
                    //数据存储
                    UserCard[] userCards=cards.toArray(new UserCard[0]);
                    Factory.getUserCenter().dispatch(userCards);
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {

            }
        });
    }

    /**
     * 搜索一个用户，优先本地缓存
     * 没有才从网络拉取
     *
     * @param id
     * @return
     */
    public static User search(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            return findFromNet(id);
        }
        return user;
    }

    /**
     * 优先网络，没有则本地
     *
     * @param id
     * @return
     */
    public static User searchFirstOfNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            return findFromLocal(id);
        }
        return user;
    }

    public static User findFromLocal(String id) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    private static User findFromNet(String id) {
        RemoteService remoteService = NetWork.remote();
        try {
            Response<RspModel<UserCard>> response = remoteService.userFind(id).execute();
            UserCard userCard = response.body().getResult();
            if (userCard != null) {
                User user = userCard.build();
                //数据存储并通知
                Factory.getUserCenter().dispatch(userCard);
                return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询联系人
     * @return
     */
    public static List<User> getContact(){
     return     SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .queryList();
    }

    /**
     * 获取一个简单联系人列表
     * @return
     */
    // TODO: 2020/2/1 待了解DBFlow的这种操作 
    public static List<UserSampleModel> getSampleontact(){
        return     SQLite.select(User_Table.id.withTable().as("id"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .queryCustomList(UserSampleModel.class);
    }
}
