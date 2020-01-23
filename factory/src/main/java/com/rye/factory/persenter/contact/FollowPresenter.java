package com.rye.factory.persenter.contact;

import com.raizlabs.android.dbflow.list.IFlowCursorIterator;
import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.model.card.UserCard;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * CreateBy ShuQin
 * at 2020/1/22
 * 关注逻辑实现
 */
public class FollowPresenter extends BasePresenter<FollowContract.View>
        implements FollowContract.Presenter, DataSource.Callback<UserCard> {

    public FollowPresenter(FollowContract.View view) {
        super(view);
    }

    @Override
    public void follow(String id) {
        start();
        UserHelper.follow(id, this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.View view = getView();
        if (view != null) {
            Run.onUiSync(new Action() {
                @Override
                public void call() {
                    view.onFollowSucceed(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int res) {
        final FollowContract.View view = getView();
        if (view != null) {
            Run.onUiSync(new Action() {
                @Override
                public void call() {
                    view.showError(res);
                }
            });
        }
    }
}
