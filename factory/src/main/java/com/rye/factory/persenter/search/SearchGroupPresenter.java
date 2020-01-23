package com.rye.factory.persenter.search;

import com.rye.catcher.factory.presenter.BasePresenter;

/**
 * CreateBy ShuQin
 * at 2020/1/19
 */
public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView>
        implements SearchContract.Presenter {
    public SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {

    }
}
