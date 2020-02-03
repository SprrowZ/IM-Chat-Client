package com.rye.catcher.frags.search;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rye.catcher.R;
import com.rye.catcher.activities.PersonalActivity;
import com.rye.catcher.activities.SearchActivity;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.app.PresenterFragment;
import com.rye.catcher.common.widget.EmptyView;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.card.GroupCard;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.persenter.contact.FollowContract;
import com.rye.factory.persenter.contact.FollowPresenter;
import com.rye.factory.persenter.search.SearchContract;
import com.rye.factory.persenter.search.SearchGroupPresenter;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment,SearchContract.GroupView {

    @BindView(R.id.empty)
    EmptyView emptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecycleView;

    private RecyclerAdapter<GroupCard> mAdapter;


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }
    @Override
    protected void initData() {
        super.initData();
        //进来有所显示
        search("");
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter( mAdapter=new RecyclerAdapter<GroupCard>(){

            @Override
            protected int getItemViewType(int position, GroupCard groupCard) {
                return R.layout.cell_search_group_list;
            }

            @Override
            protected ViewHolder<GroupCard> onCreateViewHolder(View root, int viewType) {
                return new SearchGroupFragment.ViewHolder(root);
            }
        });

        emptyView.bind(mRecycleView);
        setPlaceHolderView(emptyView);
    }



    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void onSearchDone(List<GroupCard> groupCards) {
        mAdapter.replace(groupCards);
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }



    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCard> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_join)
        ImageView mJoin;
        private FollowContract.Presenter mFollowPresenter;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCard groupCard) {
            mPortraitView.setUp(Glide.with(SearchGroupFragment.this),groupCard.getPicture());
            mName.setText(groupCard.getName());
            //加入时间判断是否可以键入群
            mJoin.setEnabled(groupCard.getJoinAt()==null);
        }
        @OnClick(R.id.im_join)
        void onJoinClick(){
            //进入创建者的个人界面
            PersonalActivity.show(getContext(),mData.getOwnerId());
        }


    }


}
