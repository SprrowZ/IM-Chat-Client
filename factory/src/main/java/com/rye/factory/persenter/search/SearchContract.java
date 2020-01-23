package com.rye.factory.persenter.search;

import com.rye.catcher.factory.presenter.BaseContract;

import com.rye.factory.model.card.GroupCard;
import com.rye.factory.model.card.UserCard;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/19
 */
public interface SearchContract {
    interface Presenter extends BaseContract.Presenter {
        void search(String content);
    }

    //搜索人
    interface UserView extends BaseContract.View<Presenter> {
        void onSearchDone(List<UserCard> userCards);
    }

    //搜索群
    interface GroupView extends BaseContract.View<Presenter> {
        void onSearchDone(List<GroupCard> groupCards);
    }
}
