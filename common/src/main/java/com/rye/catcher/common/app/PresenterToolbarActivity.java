package com.rye.catcher.common.app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

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
        extends ToolbarActivity implements BaseContract.View<T> {
    protected T mPresenter;
    // TODO: 2020/2/1 将这个Dialog抽离出来，换成DialogFragment
    protected ProgressDialog mLoadingDialog;
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
        hideDialogLoading();
        if (mPlaceHolderView!=null){//占位布局不为空的情况下，优先使用占位布局
            mPlaceHolderView.triggerError(str);
        }else {
            zApplication.showToast(str);
        }

    }
    protected  void hideDialogLoading(){
        ProgressDialog dialog=mLoadingDialog;
        if (dialog!=null){
            mLoadingDialog=null;
            dialog.dismiss();
        }

    }
    @Override
    public void showLoading() {
        if (mPlaceHolderView!=null){
            mPlaceHolderView.triggerLoading();
        }else{//没有占位布局的时候，也显示Dialog
            ProgressDialog dialog=mLoadingDialog;
            if (dialog==null){
                dialog=new ProgressDialog(this,R.style.AppTheme_Dialog_Alert_Light);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                mLoadingDialog=dialog;
            }
            dialog.setMessage(getText(R.string.prompt_loading));
            dialog.show();
        }
    }
    protected void hideLoading(){
        hideDialogLoading();
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
