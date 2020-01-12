package com.rye.factory.persenter.user;

import com.rye.catcher.factory.presenter.BaseContract;

/**
 * CreateBy ShuQin
 * at 2020/1/12
 */
public interface UpdateInfoContract {
    interface  Presenter extends BaseContract.Presenter{
        void  update(String photoFilePath,String desc,boolean isMan);
    }
    interface  View extends  BaseContract.View<Presenter>{
             void updateSucceed();
    }
}
