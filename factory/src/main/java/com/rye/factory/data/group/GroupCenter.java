package com.rye.factory.data.group;

import com.rye.factory.model.card.GroupCard;
import com.rye.factory.model.card.GroupMemberCard;

/**
 * CreateBy ShuQin
 * at 2020/1/25
 */
public interface GroupCenter {
    //群卡片
    void dispatch(GroupCard... cards);
    //群成员的处理
    void dispatch(GroupMemberCard... cards);
}
