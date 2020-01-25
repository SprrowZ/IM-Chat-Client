package com.rye.factory.data.group;



import android.os.Build;

import androidx.annotation.RequiresApi;

import com.rye.factory.data.helper.DbHelper;
import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.model.card.GroupCard;
import com.rye.factory.model.card.GroupMemberCard;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.GroupMember;
import com.rye.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 群／群成员卡片中心的实现类
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class GroupDispatcher implements GroupCenter {
    private static GroupCenter instance;
    private Executor executor = Executors.newSingleThreadExecutor();

    public static GroupCenter instance() {
        if (instance == null) {
            synchronized (GroupDispatcher.class) {
                if (instance == null)
                    instance = new GroupDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupMemberRspHandler(cards));
    }

    private class GroupMemberRspHandler implements Runnable {
        private final GroupMemberCard[] cards;

        GroupMemberRspHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for (GroupMemberCard model : cards) {
                User user = UserHelper.search(model.getUserId());//创建人
                Group group = GroupHelper.find(model.getGroupId());
                if (user != null && group != null) {
                    GroupMember member = model.build(group, user);
                    members.add(member);
                }
            }
            if (members.size() > 0)
                DbHelper.save(GroupMember.class, members.toArray(new GroupMember[0]));
        }
    }

    private class GroupHandler implements Runnable {
        private final GroupCard[] cards;

        GroupHandler(GroupCard[] cards) {
            this.cards = cards;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : cards) {
                User owner = UserHelper.search(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            if (groups.size() > 0)
                DbHelper.save(Group.class, groups.toArray(new Group[0]));
        }
    }
}
