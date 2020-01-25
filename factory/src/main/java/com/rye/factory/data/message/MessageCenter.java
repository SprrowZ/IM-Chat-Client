package com.rye.factory.data.message;

import com.rye.factory.model.card.MessageCard;

/**
 * CreateBy ShuQin
 * at 2020/1/25
 * 消息中心，进行消息卡片的消费
 */
public interface MessageCenter {
    void  dispatch(MessageCard... cards);
}
