package com.rye.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.factory.model.db.Session;
import com.rye.factory.model.db.Session_Table;

/**
 * 会话辅助工具类
 * CreateBy ShuQin
 * at 2020/1/25
 */
public class SessionHelper {
    /**
     * 从本地查找会话
     * @param id
     * @return
     */
    public static Session findFromLocal(String id){
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
