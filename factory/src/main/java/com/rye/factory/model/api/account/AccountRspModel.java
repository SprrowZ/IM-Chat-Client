package com.rye.factory.model.api.account;

import androidx.annotation.NonNull;

import com.rye.factory.model.db.User;

/**
 * CreateBy ShuQin
 * at 2020/1/6
 * 账号Model，和服务端保持一致即可
 */
public class AccountRspModel {
    private User user;
    private String account;
    private String token;
    private boolean isBind;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
