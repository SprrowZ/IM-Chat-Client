package com.rye.factory.data.message;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.rye.factory.data.BaseDbRepository;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.Message_Table;

import java.util.Collections;
import java.util.List;

/**
 * 跟某人聊天的时候聊天列表
 * 关注的内容一定是我发给他的，或者他发给我的消息
 * CreateBy ShuQin
 * at 2020/1/27
 * 数据源
 */
public class MessageRepository extends BaseDbRepository<Message>
        implements MessageDataSource {
    //聊天对象的Id
    private String receiverId;

    public MessageRepository(String receiverId) {
        super();//千万不能少，否则泛型类型获取不到
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        // TODO: 2020/1/27 待思考查询条件，以及DbFlow的OperatorGroup
        SQLite.select().from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();

    }

    /**
     * 这个过滤一定要好好思考
     * @param message
     * @return
     */
    @Override
    protected boolean isRequired(Message message) {
        //receiverId,如果是发送者，那么Group==null的情况下，一定是发送给我的信息
        //如果消息的接受者不为空，那么消息一定是发给某个人的
        //如果这个某个人是receiverId，那么就是我需要的信息
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null)
                || (message.getReceiver() != null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
