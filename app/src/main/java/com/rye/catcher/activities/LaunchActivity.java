package com.rye.catcher.activities;



import com.rye.catcher.R;
import com.rye.catcher.frags.assist.PermissionsFragment;
import com.rye.catcher.common.app.BaseActivity;

public class LaunchActivity extends BaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();

      if(PermissionsFragment.haveAll(this,getSupportFragmentManager())){
          MainActivity.show(this);
          finish();
      }
    }
}
