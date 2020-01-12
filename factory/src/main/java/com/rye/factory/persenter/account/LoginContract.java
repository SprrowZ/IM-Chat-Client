package com.rye.factory.persenter.account;

import com.rye.catcher.factory.presenter.BaseContract;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public interface LoginContract {
    interface View extends BaseContract.View<Presenter>{
       //登陆成功
        void loginSuccess();
    }
    interface  Presenter extends BaseContract.Presenter {
       void login(String phone, String password);
    }
}
