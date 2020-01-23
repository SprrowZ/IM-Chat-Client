package com.rye.factory.persenter.contact;

import android.os.Build;


import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.AppDatabase;
import com.rye.factory.model.db.User;
import com.rye.factory.model.db.User_Table;
import com.rye.factory.persistence.Account;
import com.rye.factory.utils.DiffUiDataCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/22
 */
public class ContactPresenter extends BasePresenter<ContactContract.View>
        implements ContactContract.Presenter {
    public ContactPresenter(ContactContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        // TODO: 2020/1/22 加载数据

        //本地查询数据库
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    getView().getRecyclerAdapter().replace(tResult);
                    getView().onAdapterDataChanged();
                })
                .execute();
        //查询远端数据
        UserHelper.refreshContacts(new DataSource.Callback<List<UserCard>>() {
            @Override
            public void onDataNotAvailable(int res) {
                //网络失败，因为本地有数据，不处理错误
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataLoaded(final List<UserCard> userCards) {
                final List<User> users = new ArrayList<>();
                userCards.stream().forEach(userCard ->
                        users.add(userCard.build()));
                //存到本地,有三种方式，这里是开启事务
                DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);

                definition.beginTransactionAsync(databaseWrapper ->
                        FlowManager.getModelAdapter(User.class)
                                .saveAll(users)).build().execute();
                //网络的数据往往是新的，需要刷新一下
                //比较不同，局部刷新
                List<User> old=getView().getRecyclerAdapter().getItems();

                diff(users,old);
            }
        });
        // TODO: 2020/1/22
        //1.关注后虽然存储数据库，但是没有刷新联系人
        //2.如果刷新数据库，或者网络，就会全局刷新
        //3.本地刷新和网络刷新，再添加到界面的时候有可能会有冲突。导致数据刷新异常
        //4.如果识别数据库中已经有这条数据了
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

}
