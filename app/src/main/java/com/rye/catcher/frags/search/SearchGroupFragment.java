package com.rye.catcher.frags.search;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rye.catcher.R;
import com.rye.catcher.activities.SearchActivity;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.app.PresenterFragment;
import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.card.GroupCard;
import com.rye.factory.persenter.search.SearchContract;
import com.rye.factory.persenter.search.SearchGroupPresenter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment,SearchContract.GroupView {


    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {

    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void onSearchDone(List<GroupCard> groupCards) {

    }
}
