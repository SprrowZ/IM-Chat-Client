package com.rye.factory.data.user;

import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.model.db.User;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/25
 * 联系人数据源-----监听的是数据库中数据源的改变，DataSource中的是对数据本身改变的监听
 * 真叫一个职责分明
 */
public interface ContactDataSource {
    /**
     * 监听数据库数据的改变，反馈给应用层，交由应用层的回调去处理
     * @param callback
     */
    void load(DataSource.SuccessedCallback<List<User>> callback);

    /**
     * 销毁操作
     */
    void dispose();
}
