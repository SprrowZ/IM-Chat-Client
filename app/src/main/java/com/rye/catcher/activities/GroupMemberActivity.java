package com.rye.catcher.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rye.catcher.R;
import com.rye.catcher.common.app.PresenterToolbarActivity;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.factory.model.db.view.MemberUserModel;
import com.rye.factory.persenter.group.GroupMembersContract;
import com.rye.factory.persenter.group.GroupMembersPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupMemberActivity extends PresenterToolbarActivity<GroupMembersContract.Presenter>
        implements GroupMembersContract.View {
    private static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    private static final String KEY_GROUP_ADMIN = "KEY_GROUP_ADMIN";

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private String mGroupId;

    private boolean mIsAdmin;

    private RecyclerAdapter<MemberUserModel> mAdapter;

    //非管理员的成员界面
    public static void show(Context context, String groupId) {
        show(context, groupId, false);
    }

    //管理员的群成员界面
    public static void showAdmin(Context context, String groupId) {
        show(context, groupId, true);
    }

    private static void show(Context context, String groupId, boolean isAdmin) {
        if (TextUtils.isEmpty(groupId)) return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.putExtra(KEY_GROUP_ADMIN, isAdmin);
        context.startActivity(intent);
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_member;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        this.mGroupId = bundle.getString(KEY_GROUP_ID);
        this.mIsAdmin = bundle.getBoolean(KEY_GROUP_ADMIN);
        return !TextUtils.isEmpty(mGroupId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle(R.string.title_member_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<MemberUserModel>() {
            @Override
            protected int getItemViewType(int position, MemberUserModel memberUserModel) {
                return R.layout.cell_group_create_contact;
            }

            @Override
            protected ViewHolder<MemberUserModel> onCreateViewHolder(View root, int viewType) {
                return new GroupMemberActivity.ViewHolder(root);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //开始数据刷新
        mPresenter.refresh();
    }

    @Override
    protected GroupMembersContract.Presenter initPresenter() {
        return new GroupMembersPresenter(this);
    }

    @Override
    public RecyclerAdapter<MemberUserModel> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideLoading();
    }

    @Override
    public String getmGroupId() {
        return mGroupId;
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<MemberUserModel> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.findViewById(R.id.cb_select).setVisibility(View.GONE);
        }

        @Override
        protected void onBind(MemberUserModel model) {
            mPortrait.setUp(Glide.with(GroupMemberActivity.this), model.portrait);
            mName.setText(model.name);
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(GroupMemberActivity.this, this.mData.userId);
        }

    }
}
