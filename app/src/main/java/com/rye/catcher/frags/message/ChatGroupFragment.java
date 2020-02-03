package com.rye.catcher.frags.message;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rye.catcher.R;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.view.MemberUserModel;
import com.rye.factory.persenter.message.ChatContract;
import com.rye.factory.persenter.message.ChatGroupPresenter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this,mReceiverId);
    }

    @Override
    public void onInit(Group group) {

    }

    @Override
    public void onInitGroupMember(List<MemberUserModel> members, int moreCount) {

    }

    @Override
    public void showAdminOption(boolean isAdmin) {

    }


}
