package com.rye.catcher.common.app;


import android.content.Context;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.rye.catcher.common.widget.convention.PlaceHolderView;
import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.common.R;

/**
 * CreateBy ShuQin
 * at 2020/1/18
 */
public abstract class PresenterToolbarActivity<T extends  BaseContract.Presenter>
        extends BaseActivity implements BaseContract.View<T> {
    protected T mPresenter;

    /**
     * 初始化Presenter
     * @return
     */
    protected abstract T initPresenter();

    @Override
    protected void initBefore() {
        initPresenter();
    }

    @Override
    public void showError(int str) {
        if (mPlaceHolderView!=null){//占位布局不为空的情况下，优先使用占位布局
            mPlaceHolderView.triggerError(str);
        }else {
            zApplication.showToast(str);
        }

    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerLoading();
        }
    }
    protected void hideLoading(){
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerOk();
        }
    }
    @Override
    public void setPresenter(T presenter) {
        //View中设置Presenter
        mPresenter=presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.destroy();
        }
    }
}
