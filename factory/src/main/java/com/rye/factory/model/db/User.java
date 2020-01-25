package com.rye.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.rye.catcher.factory.model.Author;
import com.rye.factory.utils.DiffUiDataCallback;

import java.util.Date;
import java.util.Objects;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 * 根据用户卡片那块就可以确定需要返回什么信息
 */
// TODO: 2020/1/25 重写HashCode和equals----必须了解~！
@Table(database = AppDatabase.class)
public class User extends BaseModel implements Author, DiffUiDataCallback.UiDataDiffer<User> {
    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;
    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private String portrait;
    @Column
    private String desc;
    @Column
    private int sex = 0;
    //用户关注人的数量
    @Column
    private int follows;
    //用户粉丝的数量
    @Column
    private int following;
    //我与当前User的关系状态
    @Column
    private boolean isFollow;
    //我对某人的备注信息
    @Column
    private String alias;
    @Column
    private Date modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }



    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPhone(), getPortrait(), getDesc(), getSex(), getFollows(), getFollowing(), isFollow(), getAlias(), getModifyAt());
    }

    @Override
    public boolean isSame(User old) {//相当于重写equals
        return this==old || getId().equals(old.getId());
    }

    @Override
    public boolean isUiContentSame(User old) {
        return this==old ||(  Objects.equals(name,old.name)
        &&Objects.equals(portrait,old.portrait))
        &&Objects.equals(sex,old.sex)
        &&Objects.equals(isFollow,old.isFollow)
              ;
    }
}
