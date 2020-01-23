package com.rye.catcher.frags.search;



import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rye.catcher.R;
import com.rye.catcher.activities.PersonalActivity;
import com.rye.catcher.activities.SearchActivity;


import  com.rye.catcher.common.app.PresenterFragment;
import com.rye.catcher.common.widget.EmptyView;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.frags.main.ContactFragment;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.persenter.contact.FollowContract;
import com.rye.factory.persenter.contact.FollowPresenter;
import com.rye.factory.persenter.search.SearchContract;
import com.rye.factory.persenter.search.SearchUserPresenter;

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
public class SearchUserFragment extends PresenterFragment <SearchContract.Presenter>
        implements SearchActivity.SearchFragment ,SearchContract.UserView {

    @BindView(R.id.empty)
    EmptyView emptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecycleView;

    private RecyclerAdapter<UserCard> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter( mAdapter=new RecyclerAdapter<UserCard>(){

            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });

        emptyView.bind(mRecycleView);
        setPlaceHolderView(emptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        //进来有所显示
        search("");
    }

    @Override
    public void search(String content) {//调用Presenter里的方法
          mPresenter.search(content);
    }


    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchUserPresenter(this);
    }

    //数据成功后
    @Override
    public void onSearchDone(List<UserCard> userCards) {
           mAdapter.replace(userCards);
           mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
    implements FollowContract.View {
         @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
         @BindView(R.id.txt_name)
        TextView mName;
         @BindView(R.id.im_follow)
        ImageView mFollow;

         private FollowContract.Presenter mFollowPresenter;
        public ViewHolder(View itemView) {
            super(itemView);
            //?????
           new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortraitView.setUp(Glide.with(SearchUserFragment.this),userCard);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick(){
              mFollowPresenter.follow(mData.getId());
        }
        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(getContext(),mData.getId());
        }
        @Override
        public void showLoading() {
            int minSize=(int) Ui.dipToPx(getResources(),22);
            int maxSize=(int) Ui.dipToPx(getResources(),30);
            LoadingDrawable drawable=new LoadingCircleDrawable(minSize,maxSize);
            drawable.setBackgroundColor(0);
            drawable.setForegroundColor(new int[]{UiCompat.getColor(getResources(),R.color.white_alpha_208)});
            mFollow.setImageDrawable(drawable);
            drawable.start();
        }
        @Override
        public void onFollowSucceed(UserCard userCard) {
         if (mFollow.getDrawable() instanceof LoadingDrawable){
             ((LoadingDrawable)mFollow.getDrawable()).stop();
             mFollow.setImageResource(R.drawable.sel_opt_done_add);
         }
        }
        @Override
        public void showError(int str) {
            if (mFollow.getDrawable() instanceof LoadingDrawable){
              LoadingDrawable drawable= (LoadingDrawable)mFollow.getDrawable();
                 drawable.setProgress(1);
                 drawable.stop();
            }
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mFollowPresenter=presenter;
        }
    }
}
