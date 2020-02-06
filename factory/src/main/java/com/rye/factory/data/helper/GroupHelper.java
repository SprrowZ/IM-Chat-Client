package com.rye.factory.data.helper;

import android.util.Log;

import com.raizlabs.android.dbflow.list.IFlowCursorIterator;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.group.GroupCreateModel;
import com.rye.factory.model.card.GroupCard;
import com.rye.factory.model.card.GroupMemberCard;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.GroupMember;
import com.rye.factory.model.db.GroupMember_Table;
import com.rye.factory.model.db.Group_Table;
import com.rye.factory.model.db.User;
import com.rye.factory.model.db.User_Table;
import com.rye.factory.model.db.view.MemberUserModel;
import com.rye.factory.net.NetWork;
import com.rye.factory.net.RemoteService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CreateBy ShuQin
 * at 2020/1/25
 * 对群的辅助工具类
 */
public class GroupHelper {
    /**
     * 优先从本地查找群，为空则去网络请求查询
     *
     * @param groupId
     * @return
     */
    public static Group find(String groupId) {
        Group group = findFromLocal(groupId);
        if (group == null) {
            group = findFromNet(groupId);
        }
        return group;
    }

    /**
     * 从本地查找群
     *
     * @param groupId
     * @return
     */
    public static Group findFromLocal(String groupId) {
        return SQLite.select().from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    /**
     * 从后台服务器查找群
     *
     * @return
     */
    public static Group findFromNet(String gropId) {
        RemoteService service = NetWork.remote();
        try {
            Response<RspModel<GroupCard>> response = service.groupFind(gropId).execute();
            GroupCard card = response.body().getResult();
            if (card != null) {
                Factory.getGroupCenter().dispatch(card);
                User user = UserHelper.search(card.getOwnerId());
                if (user != null) {
                    return card.build(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 群的创建，网络请求
     *
     * @param model
     * @param
     */
    public static void create(GroupCreateModel model, DataSource.Callback<GroupCard> callback) {
        RemoteService service = NetWork.remote();
        service.groupCreate(model)
                .enqueue(new Callback<RspModel<GroupCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                        RspModel<GroupCard> rspModel = response.body();
                        if (rspModel.success()) {
                            GroupCard groupCard = rspModel.getResult();
                            //进行保存
                            Factory.getGroupCenter().dispatch(groupCard);
                            callback.onDataLoaded(groupCard);
                        } else {
                            Factory.decodeRspCode(rspModel, callback);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                        callback.onDataNotAvailable(R.string.data_network_error);
                    }
                });
    }


    /**
     * 搜索群---仿照搜索人来
     *
     * @param name
     * @param callback
     * @return 因为用户可能有重复点击的行为，必须有取消的动作
     */
    public static Call search(final String name, final DataSource.Callback<List<GroupCard>> callback) {
        final RemoteService remoteService = NetWork.remote();
        Call<RspModel<List<GroupCard>>> call = remoteService.groupSearch(name);
        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }

    /**
     * 刷新我的群组列表
     */
    public static void refreshGroups() {
        RemoteService service = NetWork.remote();
        service.groups("").enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupCard> groupCards = rspModel.getResult();
                    if (groupCards != null && groupCards.size() > 0) {//数据存储
                        Factory.getGroupCenter().dispatch(groupCards.toArray(new GroupCard[0]));
                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                Log.e("error", t.toString());
            }
        });
    }

    /**
     * 获取一个群的成员数量
     * @param id
     * @return
     */
    // TODO: 2020/2/1 DBFlow----selectCountOf 
    public static long getMemberCount(String id) {
        return SQLite.selectCountOf()
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();

    }

    /**
     * 从网络去刷新一个群的成员信息
     * @param group
     */
    public static void refreshGroupMember(Group group) {
        RemoteService service = NetWork.remote();
        service.groupMembers(group.getId()).enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                RspModel<List<GroupMemberCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupMemberCard> memberCards = rspModel.getResult();
                    if (memberCards != null && memberCards.size() > 0) {//数据存储
                        Factory.getGroupCenter().dispatch(memberCards.toArray(new GroupMemberCard[0]));
                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                Log.e("error", t.toString());
            }
        });
    }

    /**
     * 关联查询一个用户和群成员的表，返回一个MemberUserModel表的集合
     * @param groupId
     * @param size
     * @return
     */
    // TODO: 2020/2/1 DBFlow的关联查询
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {
        return SQLite.select(GroupMember_Table.alias.withTable().as("alias"),
                User_Table.id.withTable().as("userId"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.withTable().eq(groupId))
                .orderBy(GroupMember_Table.user_id,true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);
    }
}
