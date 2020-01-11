package com.rye.catcher.factory.presenter;

import androidx.annotation.StringRes;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 * MVP--公共的契约类
 */
public interface BaseContract {

    interface View<T extends Presenter>{
        void showError(@StringRes int str);

        void showLoading();
        //支持设置一个Presenter
        void setPresenter(T presenter);
    }
    interface  Presenter{

        void start();

        void destroy();


    }
}
