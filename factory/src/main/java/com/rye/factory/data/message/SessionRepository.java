package com.rye.factory.data.message;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.factory.data.BaseDbRepository;
import com.rye.factory.model.db.Session;
import com.rye.factory.model.db.Session_Table;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/28
 */
public class SessionRepository extends BaseDbRepository<Session> implements SessionDataSource {

    @Override
    public void load(SucceedCallback<List<Session>> callback) {
        super.load(callback);
        //查询所有
        SQLite.select().from(Session.class)
                .orderBy(Session_Table.modifyAt,false)//倒叙
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Session session) {
        //所有的会话都需要，不需要过滤
        return true;
    }

    @Override
    protected void insert(Session session) {
        //覆写方法，让新的数据添加到头部
        dataList.addLast(session);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
//        Collections.reverse(tResult);//进行一次反转，不然我们insert的时候addFirst，
//        // 插入的数据还是反的,这里个人尝试addLast
//        super.onListQueryResult(transaction, tResult);
//    }
}
