package com.rye.factory.persenter.contact;

import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.factory.Factory;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.model.db.User;
import com.rye.factory.persistence.Account;


import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * CreateBy ShuQin
 * at 2020/1/23
 */
public class PersonalPresenter extends BasePresenter<PersonalContract.View>
        implements PersonalContract.Presenter {

    private User user;
    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        final   String id=getView().getUserId();
        //线程调度
        Factory.runOnAsync(() -> {
            PersonalContract.View view=getView();
            if (view!=null){
                User user= UserHelper.searchFirstOfNet(id);
                onLoaded(user);
            }

        });
    }

    private void onLoaded(User user){
        this.user=user;
        final boolean isSelf=user.getId().equalsIgnoreCase(Account.getUserId());
        final  boolean isFollow=isSelf || user.isFollow();
        final  boolean allowSayHello=isFollow&&!isSelf;
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                final PersonalContract.View view = getView();
                if (view == null)
                    return;
               view.onLoadDone(user);
               view.setFollowStatus(isFollow);
               view.allowSayHello(allowSayHello);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}
