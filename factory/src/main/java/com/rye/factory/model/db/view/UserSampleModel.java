package com.rye.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;

import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.rye.catcher.factory.model.Author;
import com.rye.factory.model.db.AppDatabase;

/**
 * CreateBy ShuQin
 * at 2020/2/1
 */
@QueryModel(database = AppDatabase.class)
public class UserSampleModel implements Author {
    @Column
    public String id;
    @Column
    public String name;
    @Column
    public String portrait;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPortrait() {
        return portrait;
    }

    @Override
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
