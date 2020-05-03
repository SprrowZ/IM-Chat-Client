package com.rye.factory.persenter.message;

import android.text.TextUtils;

import androidx.recyclerview.widget.DiffUtil;

import com.rye.factory.data.helper.MessageHelper;
import com.rye.factory.data.message.MessageDataSource;
import com.rye.factory.model.api.message.MsgCreateModel;
import com.rye.factory.model.db.Message;
import com.rye.factory.persenter.BaseSourcePresenter;
import com.rye.factory.persistence.Account;
import com.rye.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 * 基础聊天Presenter,要对数据监听
 */
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {
    protected int mReceiverType;//接受者类型：人还是群
    protected String mReceiverId;//接收者的ID:人id或者群id

    public ChatPresenter(MessageDataSource source, View view, String receiverId, int type) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = type;
    }

    @Override
    public void pushText(String content) {
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path, long time) {
        if (TextUtils.isEmpty(path)) return;

        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(path, Message.TYPE_AUDIO)
                .attach(String.valueOf(time))
                .build();
        //进行网络发送
        MessageHelper.push(model);

    }

    @Override
    public void pushImages(String[] paths) {
        if (paths == null || paths.length == 0) return;

        for (String path : paths) {//现在还是本地地址，需要替换成oss地址，也就是要先上传
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId, mReceiverType)
                    .content(path, Message.TYPE_PIC)
                    .build();
            MessageHelper.push(model);
        }
    }

    @Override
    public boolean rePush(Message message) {
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {
            message.setStatus(Message.STATUS_CREATED);
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }

        return false;
    }

    /**
     * 数据加载的时候计算新老数据差异
     *
     * @param messages
     */
    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if (view == null) return;
        //计算新老数据的差异
        List<Message> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //进行界面刷新
        refreshData(result, messages);
        view.scrollToBottom(messages);
    }
}
