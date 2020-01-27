package com.rye.factory.data.user;

import com.rye.catcher.factory.data.DbDataSource;
import com.rye.factory.model.db.User;


/**
 * CreateBy ShuQin
 * at 2020/1/25
 * 联系人数据源-----监听的是数据库中数据源的改变，DataSource中的是对数据本身改变的监听
 * 真叫一个职责分明
 */
public interface ContactDataSource  extends DbDataSource<User> {

}
