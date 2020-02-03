package com.rye.factory.persenter.message;

import com.rye.factory.data.helper.UserHelper;

import com.rye.factory.data.message.MessageRepository;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.User;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 */
public class ChatUserPresenter  extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter{


    public ChatUserPresenter(  ChatContract.UserView view, String receiverId) {
        //数据源，View，接收者，接收者的类型
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();
        //从本地查找接收者
        User mReceiver= UserHelper.findFromLocal(mReceiverId);
        getView().onInit(mReceiver);
    }
}
