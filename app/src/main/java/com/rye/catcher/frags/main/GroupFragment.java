package com.rye.catcher.frags.main;


import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rye.catcher.R;
import com.rye.catcher.activities.MessageActivity;
import com.rye.catcher.activities.PersonalActivity;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.app.PresenterFragment;
import com.rye.catcher.common.widget.EmptyView;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.factory.model.db.Group;

import com.rye.factory.persenter.group.GroupsContract;
import com.rye.factory.persenter.group.GroupsPresenter;

import butterknife.BindView;


/**
 * 参照-----ContactFragment
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends PresenterFragment<GroupsContract.Presenter> implements GroupsContract.View {
    @BindView(R.id.empty)
    EmptyView emptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecycleView;
    //与搜索不同的是，这里用User；因为与本地数据库挂钩
    private RecyclerAdapter<Group> mAdapter;
    public GroupFragment() {
        // Required empty public constructor
    }
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycleView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecycleView.setAdapter(mAdapter = new RecyclerAdapter<Group>() {

            @Override
            protected int getItemViewType(int position, Group userCard) {
                return R.layout.cell_group_list;
            }

            @Override
            protected ViewHolder<Group> onCreateViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }
        });
        //事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group group) {
                //跳转到聊天界面
                MessageActivity.show(getContext(), group);
            }
        });

        emptyView.bind(mRecycleView);
        setPlaceHolderView(emptyView);
    }

    @Override
    protected GroupsContract.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();//内部调用GroupRepository里的load方法加载数据
    }

    @Override
    public RecyclerAdapter<Group> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Group> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_desc)
        TextView mDesc;
        @BindView(R.id.txt_member)
        TextView mMember;



        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            //--PortraitView里新增方法
            mPortraitView.setUp(Glide.with(GroupFragment.this), group.getPicture());
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());

            if (group.holder!=null && group.holder instanceof String){
                mMember.setText((String)group.holder);
            }else{
                mMember.setText("");
            }


        }

    }
}
