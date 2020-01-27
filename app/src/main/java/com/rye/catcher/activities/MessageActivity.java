package com.rye.catcher.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.Operator;
import com.rye.catcher.R;
import com.rye.catcher.common.app.BaseActivity;
import com.rye.catcher.factory.model.Author;
import com.rye.catcher.frags.message.ChatGroupFragment;
import com.rye.catcher.frags.message.ChatUserFragment;
import com.rye.factory.model.db.Group;

public class MessageActivity extends BaseActivity {
    //聊天人的id，也可能是群
    public static final String KEY_RECEIVER_ID = " KEY_RECEIVER_ID";
    //是群还是人
    private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";

    private String mReceiverId;
    private boolean mIsGroup;

    /**
     * 单聊入口
     * @param context
     * @param author
     */
    public static void show(Context context, Author author) {
        if (author == null || context == null || author.getId() == null) return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

    /**
     * 群聊入口
     * @param context
     * @param group
     */
    public static void show(Context context, Group group) {
        if (group == null || context == null || group.getId() == null) return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
        context.startActivity(intent);
    }
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId=bundle.getString(KEY_RECEIVER_ID);
        mIsGroup=bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        if (mIsGroup){
            fragment=new ChatGroupFragment();
        }else {
            fragment=new ChatUserFragment();
        }
        Bundle bundle=new Bundle();
        bundle.putString(KEY_RECEIVER_ID,mReceiverId);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container,fragment)
                .commit();


    }
}
