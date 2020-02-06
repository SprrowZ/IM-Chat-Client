package com.rye.factory.persenter.group;

import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.db.view.MemberUserModel;

/**
 * CreateBy ShuQin
 * at 2020/2/3
 */
public interface GroupMembersContract {
    interface Presenter extends BaseContract.Presenter{
         void refresh();//刷新方法，checkbox的显示与否
    }

    interface  View extends BaseContract.RecyclerView<Presenter, MemberUserModel>{
          //获取群的id
        String getmGroupId();
    }


}
