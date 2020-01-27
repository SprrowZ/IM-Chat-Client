package com.rye.catcher.factory.data;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/26
 * 基础的数据库数据源接口定义
 */
public interface DbDataSource<Data> extends DataSource {
    //有一个基本的数据源加载方法，一般回调到Presenter
    void load(SuccessedCallback<List<Data>> callback);
}
