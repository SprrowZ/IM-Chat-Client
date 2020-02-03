package com.rye.factory.persenter.group;

import androidx.recyclerview.widget.DiffUtil;

import com.rye.factory.data.group.GroupsDataSource;
import com.rye.factory.data.group.GroupsRepository;
import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.model.db.Group;
import com.rye.factory.persenter.BaseSourcePresenter;
import com.rye.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 我的群组Presenter
 * * CreateBy ShuQin
 * at 2020/2/1
 */
public class GroupsPresenter extends BaseSourcePresenter<Group, Group,
        GroupsDataSource, GroupsContract.View> implements GroupsContract.Presenter{
    public GroupsPresenter(GroupsContract.View view) {
        super(new GroupsRepository(), view);
    }

    @Override
    public void start() {
        super.start();
        //加载网络数据，以后可以优化到用户下拉刷新逻辑里面，
        //只有用户下拉刷新才进行网络请求
        GroupHelper.refreshGroups();
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        final GroupsContract.View view = getView();
        if (view == null) return;
        List<Group> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(old, groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //差异更新群组
        refreshData(result, groups);
    }
}
