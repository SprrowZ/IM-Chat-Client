package com.rye.catcher.frags.account;


import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.rye.catcher.R;
import com.rye.catcher.activities.MainActivity;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.app.PresenterFragment;
import com.rye.factory.persenter.account.RegisterContract;
import com.rye.factory.persenter.account.RegisterPresenter;

import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter>
        implements RegisterContract.View {
    private AccountTrigger mAccountTrigger;
    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_password)
    EditText mPassword;
    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //拿到Activity引用
        mAccountTrigger = (AccountTrigger) context;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        mPresenter.register(phone, name, password);
    }

    @OnClick(R.id.txt_go_login)
    void onShowLoginClick() {
        //切换fragment
        mAccountTrigger.triggerView();
    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        //初始化Presenter
        return new RegisterPresenter(this);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mLoading.stop();
        //让控件可以输入
        mPhone.setEnabled(true);
        mName.setEnabled(true);
        mPassword.setEnabled(true);
        //提交按钮可以点击
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        //正在进行时，界面不可操作
        mLoading.start();
        //让控件不可以输入
        mPhone.setEnabled(false);
        mName.setEnabled(false);
        mPassword.setEnabled(false);

        mSubmit.setEnabled(false);
    }

    @Override
    public void registerSuccess() {
        //注册成功，账户已经登录
        MainActivity.show(getContext());
        getActivity().finish();
    }
}
