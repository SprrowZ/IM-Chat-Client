package com.rye.catcher.factory.presenter;

import androidx.annotation.StringRes;

import com.rye.catcher.common.widget.recycler.RecyclerAdapter;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 * MVP--公共的契约类
 */
public interface BaseContract {
     //基本界面职责
    interface View<T extends Presenter>{
        void showError(@StringRes int str);

        void showLoading();
        //支持设置一个Presenter
        void setPresenter(T presenter);
    }
    interface  Presenter{

        void start();

        void destroy();
    }
    //基本的列表View的职责
    interface  RecyclerView<T extends Presenter,ViewMode > extends View<T>{
        //界面端只能全局刷，不能定位到个体
        //  void onDone(List<User> users);
        //拿到一个适配器，然后自主进行刷新
        RecyclerAdapter<ViewMode> getRecyclerAdapter();
        //当数据该改变的时候调用，这样就可以进行局部刷新
        void onAdapterDataChanged();
    }
}
