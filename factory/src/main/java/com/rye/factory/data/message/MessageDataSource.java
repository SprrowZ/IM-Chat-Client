package com.rye.factory.data.message;

import com.rye.catcher.factory.data.DbDataSource;
import com.rye.factory.model.db.Message;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 * 消息的数据源定义---实现是：MessageRepository
 */
public interface MessageDataSource extends DbDataSource<Message> {
}
