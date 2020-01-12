package com.rye.factory.persenter.account;

import android.text.TextUtils;

import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.factory.R;
import com.rye.factory.data.helper.AccountHelper;
import com.rye.factory.model.api.account.LoginModel;
import com.rye.factory.model.db.User;
import com.rye.factory.persistence.Account;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * CreateBy ShuQin
 * at 2020/1/11
 * 登录逻辑实现
 */
public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter, DataSource.Callback<User> {
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void login(String phone, String password) {
       start();
       final  LoginContract.View view=getView();
       if (TextUtils.isEmpty(password)||TextUtils.isEmpty(password)){
           view.showError(R.string.data_account_login_invalid_parameter);
       }else{
           //传递PushId，进行绑定
           LoginModel model=new LoginModel(phone,password, Account.getPushId());
           AccountHelper.Login(model,this);
       }
    }

    @Override
    public void onDataLoaded(User user) {
        final LoginContract.View view = getView();
        if (view == null) {
            return;
        }
        //并不保证是主线程，需要进行线程切换
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                //调用主界面注册成功
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int res) {
        final  LoginContract.View view=getView();
        if (view==null){
            return;
        }
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(res);
            }
        });
    }
}
