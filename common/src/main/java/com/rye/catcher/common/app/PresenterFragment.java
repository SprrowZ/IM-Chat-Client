package com.rye.catcher.common.app;

import android.content.Context;

import com.rye.catcher.factory.presenter.BaseContract;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public abstract class PresenterFragment<T extends  BaseContract.Presenter>
        extends BaseFragment
        implements BaseContract.View<T> {
    protected T mPresenter;

    /**
     * 初始化Presenter
     * @return
     */
    protected abstract T initPresenter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //初始化Presenter
        initPresenter();
    }

    @Override
    public void showError(int str) {
        zApplication.showToast(str);
    }

    @Override
    public void showLoading() {
        // TODO: 2020/1/5 --显示Loading
    }

    @Override
    public void setPresenter(T presenter) {
        //View中设置Presenter
       mPresenter=presenter;
    }
}
