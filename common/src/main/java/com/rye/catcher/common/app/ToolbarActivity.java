package com.rye.catcher.common.app;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.rye.common.R;

/**
 * CreateBy ShuQin
 * at 2020/1/18
 */
public abstract class ToolbarActivity extends BaseActivity {
    protected Toolbar mToolbar;
    @Override
    protected void initWidget() {
        super.initWidget();
        initToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    /**
     * 初始化Toolbar
     * @param toolbar
     */
    protected void initToolbar( Toolbar toolbar){
        mToolbar=toolbar;
        if (toolbar!=null){//这哥十分重要！！！
            setSupportActionBar(mToolbar);
        }
        initTitleNeedBack();
    }

    // TODO: 2020/1/18 待了解
    protected void initTitleNeedBack(){
        //设置左上角的返回按钮为实际的返回效果
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
