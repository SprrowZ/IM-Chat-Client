package com.rye.catcher.frags.account;


import android.content.Context;

import androidx.fragment.app.Fragment;

import com.rye.catcher.R;
import com.rye.catcher.common.app.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {
    private AccountTrigger mAccountTrigger;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //拿到Activity引用
        mAccountTrigger=(AccountTrigger)context;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onResume() {
        super.onResume();
        //切换Fragment
        mAccountTrigger.triggerView();
    }
}
