package com.rye.common.mvptest;

/**
 * CreateBy ShuQin
 * at 2019/12/25
 */
public class UserModel  implements  IUserModel{
    @Override
    public String search(int hashcode){
        return "User:"+hashcode;
    }
}
