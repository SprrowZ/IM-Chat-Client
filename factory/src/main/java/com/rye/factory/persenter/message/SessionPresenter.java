package com.rye.factory.persenter.message;

import android.content.Context;

import androidx.recyclerview.widget.DiffUtil;

import com.rye.factory.data.message.SessionDataSource;
import com.rye.factory.data.message.SessionRepository;
import com.rye.factory.model.db.Session;
import com.rye.factory.persenter.BaseSourcePresenter;
import com.rye.factory.persistence.Account;
import com.rye.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/28
 */
public class SessionPresenter extends BaseSourcePresenter<Session, Session,
        SessionDataSource,SessionContract.View> implements SessionContract.Presenter {
    public SessionPresenter( SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view=getView();
        if (view==null)
            return;
        //差异对比
        List<Session> old=view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Session> callback=new DiffUiDataCallback<>(old,sessions);
        DiffUtil.DiffResult result=DiffUtil.calculateDiff(callback);
        refreshData(result,sessions);
    }

    @Override
    public void logout() {
        SessionContract.View view=getView();
        if (view==null)
            return;
        Account.logout();
        view.logoutSuccess();
    }
}
