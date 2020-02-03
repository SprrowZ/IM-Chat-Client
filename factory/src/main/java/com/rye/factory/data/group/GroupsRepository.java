package com.rye.factory.data.group;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.list.IFlowCursorIterator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.factory.data.BaseDbRepository;
import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.Group_Table;
import com.rye.factory.model.db.view.MemberUserModel;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/2/1
 */
public class GroupsRepository extends BaseDbRepository<Group> implements GroupsDataSource {

    @Override
    public void load(SucceedCallback<List<Group>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Group.class)
                .orderBy(Group_Table.name,true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Group group) {
        //一个群的信息，只有两种情况出现在数据库
        //一种是被加入群，第二种是直接建立一个群
        //无论什么情况，拿到的都只是群的信息，没有成员信息
        //需要进行成员信息初始化操作
        if (group.getMemberCount()>0){
            //已经初始化了成员的信息
            group.holder=buildGroupHolder(group);
        }else{
            //待初始化的成员信息
            group.holder=null;
            GroupHelper.refreshGroupMember(group);
        }
        //所有的我的群我都需要关注
        return true;
    }
    //初始化界面显示的成员信息
    private String buildGroupHolder(Group group) {
        //拿到成员信息，拼接名字
        List<MemberUserModel> userModels=group.getLatelyGroupMembers();
        if (userModels==null||userModels.size()==0){
            return null;
        }

        StringBuilder builder=new StringBuilder();

        for (MemberUserModel userModel:userModels){
            builder.append(TextUtils.isEmpty(userModel.alias)?userModel.name:userModel.alias);
            builder.append(",");
        }
        builder.delete(builder.lastIndexOf(","),builder.length());
        return builder.toString();
    }
}
