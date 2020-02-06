package com.rye.factory.persenter.group;

import com.rye.catcher.factory.presenter.BaseRecyclerPresenter;
import com.rye.factory.Factory;
import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.model.db.view.MemberUserModel;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/2/3
 */
public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel,GroupMembersContract.View>
implements GroupMembersContract.Presenter{
    public GroupMembersPresenter(GroupMembersContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        //显示loading
        start();
        Factory.runOnAsync(loader);
    }

    private Runnable loader= () -> {

        GroupMembersContract.View view=getView();
        if (view==null) return;
        String groupId=view.getmGroupId();
        //传递-1，代表查询所有
        List<MemberUserModel> models= GroupHelper.getMemberUsers(groupId,-1);
        //刷新界面
        refreshData(models);
    };
}
