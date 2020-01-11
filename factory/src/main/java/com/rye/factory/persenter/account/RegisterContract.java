package com.rye.factory.persenter.account;

import com.rye.catcher.factory.presenter.BaseContract;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public interface RegisterContract {
    interface View extends BaseContract.View<Presenter>{
       //注册成功
        void registerSuccess();

    }
    interface  Presenter extends BaseContract.Presenter {
       void register(String phone,String name,String password);

       boolean checkMobile(String phone);

    }
}
