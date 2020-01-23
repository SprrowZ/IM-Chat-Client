package com.rye.catcher.frags.main;


import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.rye.catcher.R;
import com.rye.catcher.activities.MessageActivity;

import com.rye.catcher.activities.PersonalActivity;
import com.rye.catcher.common.app.PresenterFragment;
import com.rye.catcher.common.widget.EmptyView;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;

import com.rye.factory.model.db.User;
import com.rye.factory.persenter.contact.ContactContract;
import com.rye.factory.persenter.contact.ContactPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends PresenterFragment<ContactContract.Presenter>
        implements ContactContract.View {
    @BindView(R.id.empty)
    EmptyView emptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecycleView;
    //与搜索不同的是，这里用User；因为与本地数据库挂钩
    private RecyclerAdapter<User> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(mAdapter = new RecyclerAdapter<User>() {

            @Override
            protected int getItemViewType(int position, User userCard) {
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }
        });
        //事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                //跳转到聊天界面
                MessageActivity.show(getContext(), user);
            }
        });

        emptyView.bind(mRecycleView);
        setPlaceHolderView(emptyView);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //进行界面操作
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_desc)
        TextView mDesc;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            //--PortraitView里新增方法
            mPortraitView.setUp(Glide.with(ContactFragment.this), user);
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            // 显示信息
            PersonalActivity.show(getContext(), mData.getId());
        }

    }
}
