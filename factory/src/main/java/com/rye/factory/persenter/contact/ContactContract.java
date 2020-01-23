package com.rye.factory.persenter.contact;

import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.db.User;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/22
 */
public interface ContactContract {
    interface  Presenter extends BaseContract.Presenter{
      //调用start方法，所以这里是空实现
    }
    interface  View extends BaseContract.RecyclerView<Presenter,User>{
        //已经再基类中完成了
    }

}
