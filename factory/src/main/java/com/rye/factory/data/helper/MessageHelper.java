package com.rye.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.api.message.MsgCreateModel;
import com.rye.factory.model.card.MessageCard;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.Message_Table;
import com.rye.factory.net.NetWork;
import com.rye.factory.net.RemoteService;

import net.qiujuer.genius.kit.handler.Run;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * CreateBy ShuQin
 * at 2020/1/25
 */
public class MessageHelper {
    public static Message findFromLocal(String id) {
      return SQLite.select()
              .from(Message.class)
              .where(Message_Table.id.eq(id))
              .querySingle();
    }

    /**
     * 发送是异步的
     * @param model
     */
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(() -> { // TODO: 2020/1/29 可以换个线程池？
            //成功状态：如果是一个已经发送过的消息，则不能重新发送
            //正在发送状态：如果是一个消息正在发送，则不能重新发送
          Message message=findFromLocal(model.getId());
          if (message!=null && message.getStatus()!=Message.STATUS_FAILED)
          return;
            //如果是文件类型的(语音/图片/文件)，先上传才发送
            // TODO: 2020/1/27
            final MessageCard card=model.buildCard();
            Factory.getMessageCenter().dispatch(card);
            //直接发送
            RemoteService service= NetWork.remote();
            service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                @Override
                public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                      RspModel<MessageCard> rspModel=response.body();
                      if (rspModel!=null&&rspModel.success()){
                          MessageCard rspCard=rspModel.getResult();
                          if (rspCard!=null){
//                                  card.setStatus(Message.STATUS_DONE);
                              Factory.getMessageCenter().dispatch(rspCard);
                          }
                      }else{
                          //检查账号是否异常
                          Factory.decodeRspCode(rspModel,null);
                          onFailure(call,null);
                      }
                }

                @Override
                public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                    card.setStatus(Message.STATUS_FAILED);
                    //失败了刷新一下界面
                    Factory.getMessageCenter().dispatch(card);
                }
            });
        });
    }

    /**
     * 查询一个消息，这个消息是一个群中的最后一条消息
     * @param groupId
     * @return
     */
    // TODO: 2020/1/29 将最后一条消息最为会话的属性 
    public static Message findLastWithGroup(String groupId) {
        return  SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt,false)
                .querySingle();
    }

    /**
     * 查询一个消息，和一个人的最后一条
     * @param userId
     * @return
     */
    public static Message findLastWithUser(String userId) {
        return  SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()//最后一条消息的发送者不一定是我，也可能是他
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt,false)
                .querySingle();
    }
}
