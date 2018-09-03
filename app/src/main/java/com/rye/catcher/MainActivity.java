package com.rye.catcher;

import android.widget.TextView;

import com.rye.common.app.BaseActivity;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
   @BindView(R.id.txt_test)
    TextView txt_test;
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        txt_test.setText("天地无极");
    }
}
