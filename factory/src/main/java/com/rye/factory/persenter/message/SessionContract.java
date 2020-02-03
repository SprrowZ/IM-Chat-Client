package com.rye.factory.persenter.message;

import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.db.Session;
import com.rye.factory.model.db.User;


/**
 * CreateBy ShuQin
 * at 2020/1/28
 */
public interface SessionContract {
    interface  Presenter extends BaseContract.Presenter{
        //调用start方法，所以这里是空实现
    }
    interface  View extends BaseContract.RecyclerView<Presenter, Session>{
        //已经再基类中完成了
    }
}
