package com.rye.factory.data.user;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.data.helper.DbHelper;
import com.rye.factory.model.db.User;
import com.rye.factory.model.db.User_Table;
import com.rye.factory.persistence.Account;


import java.util.Arrays;

import java.util.LinkedList;
import java.util.List;


/**
 * CreateBy ShuQin
 * at 2020/1/25
 * 联系人仓库---
 */
public class ContactRepository implements ContactDataSource,
        QueryTransaction.QueryResultListCallback<User>, DbHelper.ChangeListener<User> {
    private DataSource.SuccessedCallback<List<User>> callback;
    //多为增改
    private final List<User> users = new LinkedList<>();

    @Override
    public void load(DataSource.SuccessedCallback<List<User>> callback) {
        this.callback = callback;
        //对User进行监听
        DbHelper.addChangeListener(User.class, this);
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this::onListQueryResult)
                .execute();
    }

    @Override
    public void dispose() {
        this.callback = null;
        DbHelper.removeChangeListener(User.class, this);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<User> tResult) {
        //数据库加载成功
          if (tResult.size()==0){
              users.clear();
              notifyDataChanged();
              return;
          }
          User[] users=tResult.toArray(new User[0]);
          onDataSaved(users);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataSaved(User... list) {
        final boolean[] isChanged = {false};
        Arrays.asList(list).stream().forEach(user -> {
            if (isRequired(user)) {
                insertOrUpdate(user);
                isChanged[0] =true;
            }
        });
        if (isChanged[0]){
            notifyDataChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataDeleted(User... list) {
        final boolean[] isChanged = {false};
        Arrays.asList(list).stream().forEach(user -> {
            if (users.remove(user)){
                isChanged[0] =true;
            }
        });
        //有数据变更才进行刷新
        if (isChanged[0]){
            notifyDataChanged();
        }
    }

    /**
     * 插入数据或者更新数据，判断是否有再操作
     *
     * @param user
     */

    private void insertOrUpdate(User user) {
        int index = indexOf(user);
        if (index >= 0) {//下标大于0，说明是更新
            replace(index, user);
        } else {//下标为-1，说明是插入
            insert(user);
        }
    }

    private void insert(User user) {
        users.add(user);
    }

    private void replace(int index, User user) {
        if (users.size() > 0) {//判断不用加，因为下标为-1，不会进入到此方法中
            users.remove(index);
            users.add(index, user);
        }
    }


    /**
     * 这里判断必须用isSame方法，用户名字或者其他不同的时候，
     * 只要id一样，就是同一条数据
     *
     * @param user
     * @return
     */
    private int indexOf(User user) {
        int index = -1;
        for (User user1 : users) {
            index++;
            if (user1.isSame(user)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * 通知给Presenter数据已经进行更改了
     */
    private void notifyDataChanged(){
        if (callback!=null){
            callback.onDataLoaded(users);
        }
    }


    /**
     * 是我关注且不是我，才是我所需要的数据
     *
     * @param user
     * @return
     */
    private boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }
}
