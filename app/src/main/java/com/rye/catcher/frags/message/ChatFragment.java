package com.rye.catcher.frags.message;

import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.rye.catcher.R;
import com.rye.catcher.activities.MessageActivity;
import com.rye.catcher.common.app.PresenterFragment;
import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.common.tools.AudioPlayHelper;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.adapter.TextWatcherApapter;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.face.Face;
import com.rye.catcher.frags.panel.PanelFragment;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.User;
import com.rye.factory.persenter.message.ChatContract;
import com.rye.factory.persistence.Account;
import com.rye.factory.utils.FileCache;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.io.File;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 * 单聊和群聊的父类
 */
public abstract class ChatFragment<InitModel> extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel>, PanelFragment.PanelCallback {

    private static final String TAG="ChatFragment";

    protected String mReceiverId;
    protected boolean fromContact=false;
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
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingBarLayout;
    //空气面板,控制顶部面板与键盘过度的控件
    // TODO: 2020/2/3 抽离出来，不要依赖三方库
    private AirPanel.Boss mPanelBoss;

    private PanelFragment mPanelFragment;

    FileCache<AudioHolder> mAudioFileCache;

    private AudioPlayHelper<AudioHolder> mAudioPlayer;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
        fromContact=bundle.getBoolean(MessageActivity.KEY_FROM_CONTACT);
    }

    @Override   //final，子类不可再覆写
    protected final int getContentLayoutId() {
        return R.layout.fragment_chat_common;
    }

    //群聊、单聊占位布局
    @LayoutRes
    protected abstract int getHeaderLayoutId();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initWidget(View root) {
        //必须在ButterKnife.bind前拿到布局
        ViewStub stub = (ViewStub) root.findViewById(R.id.view_stub_header);
        stub.setLayoutResource(getHeaderLayoutId());
        stub.inflate();
        super.initWidget(root);
        //空气面板
        mPanelBoss = root.findViewById(R.id.lay_content);
        mPanelBoss.setup(() -> {
            //请求隐藏软键盘
            Util.hideKeyboard(mContent);
        });
        mPanelBoss.setOnStateChangedListener(new AirPanel.OnStateChangedListener() {
            @Override
            public void onPanelStateChanged(boolean isOpen) {
                //面板改变
                if (isOpen) {
                    onBottomPanelOpened();
                }
            }

            @Override
            public void onSoftKeyboardStateChanged(boolean isOpen) {

            }
        });
        //拿到面板Fragment
        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setup(this);


        initToolbar();
        initAppbar();
        initEditContent();
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecycleView.setAdapter(mAdapter);
        //定位到最底部的消息


        //语言点击下载
        // TODO: 2020/2/5 将Item的点击事件抽离出去，长按用Dialog实现
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Message>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Message message) {
                if (message.getType() == Message.TYPE_AUDIO && holder instanceof ChatFragment.AudioHolder) {
                    mAudioFileCache.download((ChatFragment.AudioHolder) holder, message.getContent());
                }
            }
        });
        //如果来自聊天会话界面，RecycleView需要设置一个marginBottom，否则最后一条不显示
//        if (fromContact){
//            CoordinatorLayout.LayoutParams params= (CoordinatorLayout.LayoutParams) mRecycleView.getLayoutParams();
//            params.bottomMargin= (int) Ui.dipToPx(getResources(),50);//头像高度
//            mRecycleView.setLayoutParams(params);
//        }
//         mRecycleView.setOnFlingListener(new RecyclerView.OnFlingListener() {
//             @Override
//             public boolean onFling(int velocityX, int velocityY) {
//                 Log.i(TAG,"RecycleView bottom:");
//                 if (((CoordinatorLayout.LayoutParams) mRecycleView.getLayoutParams()).bottomMargin!=0){
//                     Log.i(TAG,"RecycleView bottom:2");
//                     CoordinatorLayout.LayoutParams params= (CoordinatorLayout.LayoutParams) mRecycleView.getLayoutParams();
//                     params.bottomMargin= (int) Ui.dipToPx(getResources(),0);//头像高度
//                     mRecycleView.setLayoutParams(params);
//                 }
//                 return false;
//             }
//         });

    }

    @Override
    public void scrollToBottom(List<Message> dataList) {
        if (dataList == null || mRecycleView == null) return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                Log.i(TAG,"dataSize:"+dataList.size()+"---"+mRecycleView.canScrollVertically(1));
                mRecycleView.scrollToPosition(mAdapter.getItemCount()-1);
            }
        });
    }

    //底部面板打开，头部折叠
    private void onBottomPanelOpened() {
        if (mAppbar != null) {
            mAppbar.setExpanded(false, true);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        //进行初始化操作
        mPresenter.start();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAudioPlayer = new AudioPlayHelper(new AudioPlayHelper.RecordPlayListener<AudioHolder>() {
            @Override
            public void onPlayStart(AudioHolder audioHolder) {
                audioHolder.onPlayStart();
            }

            @Override
            public void onPlayStop(AudioHolder audioHolder) {
                audioHolder.onPlayStop();
            }

            @Override
            public void onPlayError(AudioHolder audioHolder) {
                zApplication.showToast(R.string.toast_audio_play_error);
            }
        });


        //下载工具类
        mAudioFileCache = new FileCache("audio/cache", "mp3", new FileCache.CacheListener<ChatFragment.AudioHolder>() {
            @Override
            public void onDownloadSucceed(ChatFragment.AudioHolder audioHolder, File file) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        mAudioPlayer.trigger(audioHolder, file.getAbsolutePath());
                    }
                });
            }

            @Override
            public void onDownloadFailed(ChatFragment.AudioHolder audioHolder) {
                zApplication.showToast(R.string.toast_download_error);
            }

        });
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

    //空气面板回调
    @Override
    public EditText getInputEditText() {
        return mContent;
    }

    @Override
    public void onSendGallery(String[] paths) {
        //发送图片
        mPresenter.pushImages(paths);
    }

    @Override
    public void onRecordDone(File file, long time) {
        mPresenter.pushAudio(file.getAbsolutePath(), time);
    }

    /**
     * CollapsingToolbarLayout展开的时候，RecycleView定位到最后一条不对，需要设置一个marginBottom
     * @param appBarLayout
     * @param i
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
              Log.i(TAG,"AppBarLayout current Pos:"+i);
    }

    /**
     * 拦截返回键
     *
     * @return
     */
    @Override
    public boolean onBackPressed() {
        if (mPanelBoss.isOpen()) {//面板打开关闭面板，否则退出聊天界面
            mPanelBoss.closePanel();
        } else {
            getActivity().finish();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioPlayer.destroy();
    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {//表情
        //请求打开
        mPanelBoss.openPanel();
        mPanelFragment.showFace();
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {//录音
        //请求打开
        mPanelBoss.openPanel();
        mPanelFragment.showRecord();
    }

    private void onMoreClick() {//图片
        //请求打开
        mPanelBoss.openPanel();
        mPanelFragment.showGallery();
    }


    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            //发送
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //没有占位布局，空实现
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
            if (mLoading != null && mPresenter.rePush(mData)) {
                //必须是右边才有可能重新发送,状态改变需要重新刷新界面
                updataData(mData);
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
            Spannable spannable = new SpannableString(message.getContent());
            //解析表情资源
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));
            //把内容设置到布局上
            mContent.setText(spannable);

        }
    }


    class AudioHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.im_audio_track)
        ImageView mAudioTrack;


        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            String attach = TextUtils.isEmpty(message.getAttach()) ? "0" : message.getAttach();
            mContent.setText(formatTime(attach));

        }

        void onPlayStart() {
            mAudioTrack.setVisibility(View.VISIBLE);
        }

        void onPlayStop() {
            mAudioTrack.setVisibility(View.INVISIBLE);
        }

        private String formatTime(String attach) {
            float time;
            try {
                // 毫秒转换为秒
                time = Float.parseFloat(attach) / 1000f;
            } catch (Exception e) {
                time = 0;
            }
            // 12000/1000f = 12.0000000
            // 取整一位小数点 1.234 -> 1.2 1.02 -> 1.0
            String shortTime = String.valueOf(Math.round(time * 10f) / 10f);
            // 1.0 -> 1     1.2000 -> 1.2
            shortTime = shortTime.replaceAll("[.]0+?$|0+?$", "");
            return String.format("%s″", shortTime);
        }
    }

    class PicHolder extends BaseHolder {
        @BindView(R.id.im_image)
        ImageView mContent;

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //当是图片类型的时候，content中就是具体的地址
            String content = message.getContent();
            RequestOptions options = new RequestOptions();
            options.fitCenter();
            Glide.with(ChatFragment.this)
                    .load(content)
                    .apply(options)
                    .into(mContent);
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
