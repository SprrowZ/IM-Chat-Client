package com.rye.factory.data.user;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import com.rye.factory.data.BaseDbRepository;
import com.rye.factory.model.db.User;
import com.rye.factory.model.db.User_Table;
import com.rye.factory.persistence.Account;

import java.util.List;


/**
 * CreateBy ShuQin
 * at 2020/1/25
 * 联系人仓库---
 */
public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource {


    @Override
    public void load(SucceedCallback<List<User>> callback) {
        super.load(callback);
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
    protected boolean isRequired(User user) {
        return user.isFollow()&&!user.getId().equalsIgnoreCase(Account.getUserId());
    }


}
