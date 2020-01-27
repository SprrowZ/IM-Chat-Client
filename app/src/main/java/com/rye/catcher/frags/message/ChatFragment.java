package com.rye.catcher.frags.message;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.rye.catcher.R;
import com.rye.catcher.activities.MessageActivity;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.adapter.TextWatcherApapter;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.User;
import com.rye.factory.persistence.Account;

import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 * 单聊和群聊的父类
 */
public abstract class ChatFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    protected String mReceiverId;
    protected Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecycleView;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.edit_content)
    EditText mContent;
    @BindView(R.id.btn_submit)
    View mSubmit;


    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);

    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initToolbar();
        initAppbar();
        initEditContent();
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecycleView.setAdapter(mAdapter);

    }

    /**
     * 初始化Toolbar
     */
    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> getActivity().finish());
    }

    /**
     * 给AppBarLayout设置一个监听，设置头像的隐藏与否
     */
    private void initAppbar() {
        mAppbar.addOnOffsetChangedListener(this);
    }

    /**
     * 输入框监听
     */
    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherApapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                //设置状态，没有消息不激活
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {

    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {

    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            //发送
        } else {
            onMoreClick();
        }
    }


    private void onMoreClick() {
        // TODO: 2020/1/27
    }

    //内容适配器
    private class Adapter extends RecyclerAdapter<Message> {
        // TODO: 2020/1/27 将两个方法定义在接口中
        @Override
        protected int getItemViewType(int position, Message message) {
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {//文件内容
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                //语音
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;
                //图片
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                // TODO: 2020/1/27 文件自己做  Message.TYPE_FILE:
                default:
                    return isRight ? R.layout.cell_chat_file_right : R.layout.cell_chat_file_left;
            }

        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                case R.layout.cell_chat_text_left:
                case R.layout.cell_chat_text_right:
                    return new TextHolder(root);
                case R.layout.cell_chat_audio_left:
                case R.layout.cell_chat_audio_right:
                    return new AudioHolder(root);
                case R.layout.cell_chat_pic_left:
                case R.layout.cell_chat_pic_right:
                    return new PicHolder(root);
                default://文件待做
                    return new FileHolder(root);
            }

        }
    }

    //holder基类
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        //允许为空，左边没有，右边有
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            //进行数据加载，Message里这个字段是懒加载的
            sender.load();
            mPortraitView.setUp(Glide.with(ChatFragment.this), sender);
            //当前布局有右边，也就是我本人
            if (mLoading != null) {
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    //正常状态,隐藏Loading
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    //正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    //发送失败状态,允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }
                //只有发送失败的情况下，头像才能点击
                mPortraitView.setEnabled(status == Message.STATUS_FAILED);
            }

        }

        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            //重新发送
            if (mLoading != null) {
                //必须是右边才有可能重新发送
                // TODO: 2020/1/27
            }
        }
    }

    // TODO: 2020/1/27 待抽离Holder到单独文件中 
    //文字holder
    class TextHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //把内容设置到布局上
            mContent.setText(message.getContent());

        }
    }


    class AudioHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //把内容设置到布局上
            mContent.setText(message.getContent());

        }
    }

    class PicHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //把内容设置到布局上
            mContent.setText(message.getContent());

        }
    }

    class FileHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public FileHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //把内容设置到布局上
            mContent.setText(message.getContent());

        }
    }




}
