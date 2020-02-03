package com.rye.factory.persenter.group;


import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.User;


/**
 * 我的群列表契约
 * CreateBy ShuQin
 * at 2020/1/22
 */
public interface GroupsContract {
    interface  Presenter extends BaseContract.Presenter{
      //调用start方法，所以这里是空实现
    }
    interface  View extends BaseContract.RecyclerView<Presenter, Group>{
        //已经再基类中完成了
    }

}
