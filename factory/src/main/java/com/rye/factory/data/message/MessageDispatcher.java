package com.rye.factory.data.message;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.rye.factory.data.helper.DbHelper;
import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.data.helper.MessageHelper;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.data.user.UserDispatcher;
import com.rye.factory.model.card.MessageCard;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CreateBy ShuQin
 * at 2020/1/25
 */
public class MessageDispatcher implements MessageCenter {

    private static MessageDispatcher instance;
    /**
     * 单线程线程池
     */
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageDispatcher instance(){
        if (instance==null){
            synchronized (MessageDispatcher.class){
                if (instance==null){
                    instance=new MessageDispatcher();
                }
            }
        }
        return instance;
    }
    @Override
    public void dispatch(MessageCard... cards) {
           if (cards==null||cards.length==0) return;
           executor.execute(new MessageCardHandler(cards));
    }

    private class MessageCardHandler implements Runnable{
        private final  MessageCard[] cards;
        MessageCardHandler(MessageCard[] cards){
            this.cards=cards;
        }
        // TODO: 2020/1/25 -----------重要逻辑，需要多加关注
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId())))
                    continue;
                //消息卡片有可能是推送过来的，也有可能是自己造的
                //推送过来的服务器一定有，我们可以查询
                //如果是直接造的，需要存储到本地，然后推送到服务器
                //发消息流程：写消息->存储本地->发送服务器->网络返回->刷新本地状态
                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {
                    // 如果已经完成则不做处理
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;
                    // 新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus() == Message.STATUS_DONE) {
                        message.setCreateAt(card.getCreateAt());
                        //没进入判断说明发送失败，下次再进入的时候就相当于更新状态
                    }
                    // 更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    message.setStatus(card.getStatus());
                } else {//初次在数据库建立，新消息；接收的新消息
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.search(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    //接受者没有，群也没有，说明是条假消息
                    if (receiver == null && group == null)
                        continue;

                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if (messages.size() > 0)
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
