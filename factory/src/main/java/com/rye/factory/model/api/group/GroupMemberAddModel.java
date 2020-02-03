package com.rye.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * CreateBy ShuQin
 * at 2020/2/1
 */
public class GroupMemberAddModel {
    private Set<String> users=new HashSet<>();
    public Set<String> getUses(){
        return users;
    }
    public void setUsers(Set<String> users){
        this.users=users;
    }
}
