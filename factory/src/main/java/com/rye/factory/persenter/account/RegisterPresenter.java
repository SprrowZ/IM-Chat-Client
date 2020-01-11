package com.rye.factory.persenter.account;

import android.text.TextUtils;

import com.rye.catcher.common.Common;
import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.factory.R;
import com.rye.factory.data.helper.AccountHelper;
import com.rye.factory.model.api.account.RegisterModel;
import com.rye.factory.model.db.User;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.Callback<User> {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        //调用开始方法，在start中默认启动了Loading
        start();
        //得到View接口
        RegisterContract.View view = getView();


        //校验
        if (!checkMobile(phone)) {
            //提示
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        } else if (name.length() < 2) {
            //名字需要大于两位
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else if (password.length() < 6) {
            //密码需要大于6位
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else {
            //进行网络请求,构建Model，请求调用
            RegisterModel registerModel = new RegisterModel(phone, password, name);
            //进行网络请求，并设置回送口
            AccountHelper.register(registerModel, this);
        }
    }

    /**
     * 检查手机号是否合法
     *
     * @param phone
     * @return
     */
    @Override
    public boolean checkMobile(String phone) {
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);//都是数字
    }

    @Override
    public void onDataLoaded(User user) {
        //当网络请求陈宫，注册好了，回送一个用户信息
        //告知界面，注册成功
        final RegisterContract.View view = getView();
        if (view == null) {
            return;
        }
        //并不保证是主线程，需要进行线程切换
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                //调用主界面注册成功
                view.registerSuccess();
            }
        });

    }

    @Override
    public void onDataNotAvailable(final int res) {
        //网络请求结果失败，通知上层
        final  RegisterContract.View view=getView();
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
