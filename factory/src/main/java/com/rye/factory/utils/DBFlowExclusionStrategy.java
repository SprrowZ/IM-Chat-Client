package com.rye.factory.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

/**
 * CreateBy ShuQin
 * at 2020/1/11
 * DBFlow的数据库过滤字段Gson
 */
// TODO: 2020/1/11 待了解
public class DBFlowExclusionStrategy  implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaredClass().equals(ModelAdapter.class);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
