package com.rye.factory.persenter.contact;

import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.card.UserCard;

/**
 * CreateBy ShuQin
 * at 2020/1/22
 */
public interface FollowContract {

    interface  Presenter extends BaseContract.Presenter{
        void follow(String id);
    }
    interface  View extends BaseContract.View<Presenter>{
        void onFollowSucceed(UserCard userCard);
    }
}
