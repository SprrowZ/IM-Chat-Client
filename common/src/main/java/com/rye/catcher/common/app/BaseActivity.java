package com.rye.catcher.common.app;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rye.catcher.common.widget.convention.PlaceHolderView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by ShuQin
 */
public abstract  class BaseActivity extends AppCompatActivity {

    protected PlaceHolderView mPlaceHolderView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在界面未初始化之前调用的初始化窗口
        initWindows();
        if (initArgs(getIntent().getExtras())){
            //得到界面Id并设置到Activity界面中
            int layId=getContentLayoutId();
            setContentView(layId);
            initBefore();
            initWidget();
            initData();
        }else {
            finish();
        }

    }

    /**
     * 初始化控件之前
     */
    protected  void initBefore(){

    }
    /**
     * 初始化窗口
     */
    protected  void  initWindows(){

    }

    /**
     * 初始化相关参数
     * @param bundle 参数Bundle
     * @return 如果参数正确返回True，错误返回False
     */
    protected  boolean initArgs(Bundle bundle){
        return true;
    }
    /**
     * 返回当前资源文件ID
     * @return
     */
    protected abstract  int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(){
        ButterKnife.bind(this);//封装
    }

    /**
     * 初始化数据
     */
    protected void initData(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        //当点击页面导航时，finish掉当前页面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //如果一个界面有多个fragment，一层层掉，就不能直接finish掉了/
        // 会走Fragment中onCreateView里的mRoot移除逻辑
        List<Fragment> fragments=getSupportFragmentManager().getFragments();
        if (fragments!=null&&fragments.size()>0){
        for (Fragment fragment:fragments) {
                if (fragment instanceof BaseFragment){
                    if (((BaseFragment) fragment).onBackPressed()){
                        return;//如果拦截就交给fragment处理，否则就走下面的finish
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }
    /*
设置占位布局
 */
    public void  setPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView=placeHolderView;
    }
}
