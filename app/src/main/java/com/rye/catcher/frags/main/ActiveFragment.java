package com.rye.catcher.frags.main;


import android.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rye.catcher.R;

import com.rye.catcher.activities.MessageActivity;

import com.rye.catcher.common.app.PresenterFragment;
import com.rye.catcher.common.widget.EmptyView;

import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.face.Face;
import com.rye.catcher.utils.DateTimeUtil;
import com.rye.factory.model.db.Session;

import com.rye.factory.persenter.message.SessionContract;
import com.rye.factory.persenter.message.SessionPresenter;

import net.qiujuer.genius.ui.Ui;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
implements SessionContract.View {
    @BindView(R.id.empty)
    EmptyView emptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecycleView;
    private RecyclerAdapter<Session> mAdapter;
    public ActiveFragment() {
        // Required empty public constructor
    }
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(mAdapter = new RecyclerAdapter<Session>() {

            @Override
            protected int getItemViewType(int position, Session session) {
                return R.layout.cell_chat_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        });
        //事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                //跳转到聊天界面
                MessageActivity.show(getContext(), session);
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
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }



    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
       mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.txt_time)
        TextView mTime;


        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            //--PortraitView里新增方法
            mPortraitView.setUp(Glide.with(ActiveFragment.this), session.getPicture());
            mName.setText(session.getTitle());
             String str=TextUtils.isEmpty(session.getContent())?"":session.getContent();
             Spannable spannable=new SpannableString(str);
            //解析表情资源
            Face.decode(mContent,spannable, (int) mContent.getTextSize());
            //把内容设置到布局上
            mContent.setText(spannable);
            mTime.setText(DateTimeUtil.getSampleDate(session.getModifyAt()));
        }


    }
}
