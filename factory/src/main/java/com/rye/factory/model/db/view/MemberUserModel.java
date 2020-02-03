package com.rye.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.rye.factory.model.db.AppDatabase;

/**
 * 获取群成员的信息专用表：
 * 如果通过GroupMember去加载User，因为User过多，且两个表有太多属性不需要，只需要name
 * 所以新建简单表去关联查询
 * 临时表
 * CreateBy ShuQin
 * at 2020/2/1
 */

@QueryModel(database = AppDatabase.class)
public class MemberUserModel {
    // TODO: 2020/2/1 -----待了解QueryModel
    @Column
    public String userId;//User-id
    @Column
    public String name;//User-name
    @Column
    public String alias;//Member-alias
    @Column
    public String portrait;//User-portrait
}
