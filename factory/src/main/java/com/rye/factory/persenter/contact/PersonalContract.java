package com.rye.factory.persenter.contact;

import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.db.User;

/**
 * CreateBy ShuQin
 * at 2020/1/23
 */
public interface PersonalContract {
    interface  Presenter extends BaseContract.Presenter{
        User getUserPersonal();
    }
    interface  View extends BaseContract.View<Presenter>{
        String getUserId();
        void onLoadDone(User user);
        //是否显示发起聊天
        void allowSayHello(boolean isAllow);
        //关注状态
        void setFollowStatus(boolean isFollow);
    }
}
