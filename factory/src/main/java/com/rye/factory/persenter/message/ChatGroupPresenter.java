package com.rye.factory.persenter.message;

import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.data.message.MessageGroupRepository;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.Message;

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
        getView().onInit(group);
    }
}
