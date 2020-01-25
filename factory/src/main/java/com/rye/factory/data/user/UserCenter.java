package com.rye.factory.data.user;

import com.rye.factory.model.card.UserCard;

/**
 * CreateBy ShuQin
 * at 2020/1/24
 * 用户中心的基本定义--通过接口隐藏实现
 */
public interface UserCenter {
    //分发处理一堆卡片，并更新到数据库中
    void dispatch(UserCard... userCards);
}
