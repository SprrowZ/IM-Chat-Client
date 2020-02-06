package com.rye.factory.persenter.message;

import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.data.message.MessageGroupRepository;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.view.MemberUserModel;
import com.rye.factory.persistence.Account;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 */
public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter {


    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        //数据源，View，接收者，接收者的类型
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();
//拿群的信息
        Group group= GroupHelper.findFromLocal(mReceiverId);
        if (group!=null){
            ChatContract.GroupView view=getView();
            boolean isAdmin= Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            //是否显示加人按钮
            view.showAdminOption(isAdmin);
            getView().onInit(group);
            //成员初始化
            List<MemberUserModel> models=group.getLatelyGroupMembers();
            final long memberCount=group.getGroupMemberCount();
            long moreCount=memberCount-models.size();
            view.onInitGroupMember(models,moreCount);


        }



    }
}
