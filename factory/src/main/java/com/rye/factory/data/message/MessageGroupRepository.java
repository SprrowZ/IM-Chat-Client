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
 * 跟群组聊天的时候聊天列表
 * 复用MessageRepository
 * CreateBy ShuQin
 * at 2020/1/27
 * 数据源
 */
public class MessageGroupRepository extends BaseDbRepository<Message>
        implements MessageDataSource {
    //聊天对象的Id
    private String receiverId;

    public MessageGroupRepository(String receiverId) {
        super();//千万不能少，否则泛型类型获取不到
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

       //无论是直接发还是别人发，只要是发到这个群的，group_id就是receiverId
        SQLite.select().from(Message.class)
                .where(Message_Table.group_id.eq(receiverId))
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
        //消息Group不为空，说明是发送到群的，群Id属于我们想要的
        return message.getGroup()!=null&&
                receiverId.equalsIgnoreCase(message.getGroup().getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
