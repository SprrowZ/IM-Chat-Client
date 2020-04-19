package com.rye.catcher.common.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rye.catcher.common.widget.convention.PlaceHolderView;
import com.rye.common.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ShuQin
 */
public abstract class BaseFragment extends Fragment {
    protected View mRoot;
    protected Unbinder mRootUnbinder;
    protected PlaceHolderView mPlaceHolderView;
    //标识是否第一次初始化数据
    protected  boolean mIsFirstInitData=true;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 初始化参数
        initArgs(getArguments());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            int layId = getContentLayoutId();
            // 初始化当前的跟布局，但是不在创建时就添加到container里边
            View root = inflater.inflate(layId, container, false);
            initWidget(root);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                // 把当前Root从其父控件中移除
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }

        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData){
            mIsFirstInitData=false;
            onFirstInit();
        }
        initData();
    }

    /**
     * 初始化相关参数
     */
    protected void initArgs(Bundle bundle) {

    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    @LayoutRes
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(View root) {
        mRootUnbinder= ButterKnife.bind(this,root);//将根布局绑定到Fragment中
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }



    /**
     * 当首次初始化数据的时候会调用的方法
     */
    protected void onFirstInit() {

    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回True代表我已处理返回逻辑，Activity不用自己finish。
     * 返回False代表我没有处理逻辑，Activity自己走自己的逻辑
     */
    public boolean onBackPressed() {
        return false;
    }


    /*
    设置占位布局
     */
    public void  setPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView=placeHolderView;
    }

    /**
     * 左侧划出的动画
     * @param intent
     */
    public void startActivityLeft(Intent intent){
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_translate_out,R.anim.activity_translate_in);
    }
}
