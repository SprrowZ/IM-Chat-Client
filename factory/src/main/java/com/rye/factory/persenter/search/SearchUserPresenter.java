package com.rye.factory.persenter.search;

import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.model.card.UserCard;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * CreateBy ShuQin
 * at 2020/1/19
 */
public class SearchUserPresenter extends BasePresenter<SearchContract.UserView>
        implements SearchContract.Presenter, DataSource.Callback<List<UserCard>> {
    //可复用的Call
    private Call searchCall;

    public SearchUserPresenter(SearchContract.UserView view) {
        super(view);
    }

    @Override
    public void search(String content) {
           start();
           Call call=searchCall;//避免线程冲突
           if (call!=null && !call.isCanceled()){
               call.cancel();
           }
           searchCall= UserHelper.search(content,this);
    }

    @Override
    public void onDataLoaded(final  List<UserCard> userCards) {
         final SearchContract.UserView view=getView();
         if (view!=null){
             Run.onUiSync(new Action() {//调用并不一定在主线程，可以用handler；
                 @Override
                 public void call() {
                    view.onSearchDone(userCards);
                 }
             });
         }
    }

    @Override
    public void onDataNotAvailable(final int res) {
        final SearchContract.UserView view=getView();
        if (view!=null){
            Run.onUiSync(new Action() {
                @Override
                public void call() {
                    view.showError(res);
                }
            });
        }
    }
}
