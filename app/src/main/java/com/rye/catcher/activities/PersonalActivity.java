package com.rye.catcher.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.list.IFlowCursorIterator;
import com.rye.catcher.R;
import com.rye.catcher.common.app.BaseActivity;
import com.rye.catcher.common.app.PresenterToolbarActivity;
import com.rye.catcher.common.app.ToolbarActivity;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.factory.model.db.User;
import com.rye.factory.persenter.contact.PersonalContract;
import com.rye.factory.persenter.contact.PersonalPresenter;

import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonalActivity extends PresenterToolbarActivity<PersonalContract.Presenter>
        implements PersonalContract.View {
    private static final String BOUND_KEY_ID = "BOUND_KEY_ID";
    private String userId;

    @BindView(R.id.im_header)
    ImageView mHeader;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.txt_name)
    TextView mName;
    @BindView(R.id.txt_desc)
    TextView mDesc;
    @BindView(R.id.txt_follows)
    TextView mFollows;
    @BindView(R.id.txt_following)
    TextView mFollowing;
    @BindView(R.id.btn_say_hello)
    Button mSayHello;

    private MenuItem mFollowItem;
    private boolean mIsFollow=false;

    public static void show(Context context, String userId) {
        Intent intent = new Intent(context, PersonalActivity.class);
        intent.putExtra(BOUND_KEY_ID, userId);
        context.startActivity(intent);
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        userId = bundle.getString(BOUND_KEY_ID);
        return !TextUtils.isEmpty(userId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    // TODO: 2020/1/23  再回顾一下Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.personal, menu);
        mFollowItem = menu.findItem(R.id.action_follow);
        changeFollowItemStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_follow) {
            // 进行关注操作
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_say_hello)
    void onSayHelloClick(){
         User user=mPresenter.getUserPersonal();
        if (user == null) {
            return;
        }
        MessageActivity.show(this,user);

    }

    /**
     * 更改关注菜单状态
     */
    private void changeFollowItemStatus(){
        if (mFollowItem==null) return;

        Drawable drawable= mIsFollow?getResources().
                getDrawable(R.drawable.ic_favorite)
                :getResources().getDrawable(R.drawable.ic_favorite_border);
        drawable= DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, UiCompat.getColor(getResources(),R.color.white));
        mFollowItem.setIcon(drawable);
    }

    @Override
    protected PersonalContract.Presenter initPresenter() {
        return new PersonalPresenter(this);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onLoadDone(User user) {
        if (user==null){
            return;
        }
        mPortrait.setUp(Glide.with(this),user);
        mName.setText(user.getName());
        mDesc.setText(user.getDesc());
        mFollows.setText(String.format(getString(R.string.label_follows),user.getFollows()));
        mFollowing.setText(String.format(getString(R.string.label_following),user.getFollowing()));
        hideLoading();
    }

    @Override
    public void allowSayHello(boolean isAllow) {
        mSayHello.setVisibility(isAllow? View.VISIBLE:View.GONE);
    }

    @Override
    public void setFollowStatus(boolean isFollow) {
             mIsFollow=isFollow;
             changeFollowItemStatus();
    }
}
