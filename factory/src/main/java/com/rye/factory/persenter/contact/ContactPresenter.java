package com.rye.factory.persenter.contact;

import android.os.Build;


import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.catcher.factory.presenter.BaseRecyclerPresenter;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.data.user.ContactDataSource;
import com.rye.factory.data.user.ContactRepository;
import com.rye.factory.model.db.User;
import com.rye.factory.utils.DiffUiDataCallback;
import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/22
 */
public class ContactPresenter extends BaseRecyclerPresenter<User,ContactContract.View>
        implements ContactContract.Presenter ,DataSource.SuccessedCallback<List<User>>{
    private   ContactDataSource mSources;
    public ContactPresenter(ContactContract.View view) {
        super(view);
        mSources=new ContactRepository();
    }

    @Override
    public void start() {
        super.start();
        //本地查询数据库
         mSources.load(this);
        //查询网络数据
        UserHelper.refreshContacts();
    }

    /**
     * 比较远端数据和本地数据的异同，很可能很耗时
     * @param newList
     * @param oldList
     */
    // TODO: 2020/1/22 待了解DiffUtil
    private void diff(List<User> newList,List<User> oldList){
        DiffUiDataCallback callback=new DiffUiDataCallback(oldList,newList);
        DiffUtil.DiffResult result=DiffUtil.calculateDiff(callback);
        //在对比完成后进行数据的赋值
        getView().getRecyclerAdapter().replace(newList);
        //尝试刷新界面
        result.dispatchUpdatesTo(getView().getRecyclerAdapter());
        getView().onAdapterDataChanged();
    }

    /**
     * 必须保证在子线程中，对比是一个耗时的操作
     * @param users
     */
    @Override
    public void onDataLoaded(List<User> users) {
        //无论怎么操作，数据变更最终都会通知到这里
        final  ContactContract.View view=getView();
        if (view==null) return;
        RecyclerAdapter<User> adapter=view.getRecyclerAdapter();
        //拿到老数据
        List<User> old=adapter.getItems();
        //新老数据对比
        DiffUtil.Callback callback=new DiffUiDataCallback<>(old,users);
        DiffUtil.DiffResult result=DiffUtil.calculateDiff(callback);
        //调用基类方法进行界面刷新
        refreshData(result,users);
    }

    @Override
    public void destroy() {
        super.destroy();
        //当界面销毁时，把监听也进行销毁----------防止内存泄漏
        mSources.dispose();
    }
}
