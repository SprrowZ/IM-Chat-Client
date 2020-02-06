package com.rye.catcher.frags.message;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.rye.catcher.R;
import com.rye.catcher.activities.GroupMemberActivity;
import com.rye.catcher.activities.PersonalActivity;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.view.MemberUserModel;
import com.rye.factory.persenter.message.ChatContract;
import com.rye.factory.persenter.message.ChatGroupPresenter;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {
    @BindView(R.id.im_header)
    ImageView mHeader;

    @BindView(R.id.lay_members)
    LinearLayout mLayMembers;
    @BindView(R.id.txt_member_more)
    TextView mMemberMore;

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, mReceiverId);
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        // TODO: 2020/1/28 待替换，待了解---渐变效果
        Glide.with(this)
                .load(R.drawable.default_banner_group)
                .apply(options)
                .into(new ViewTarget<CollapsingToolbarLayout, Drawable>(mCollapsingBarLayout) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }

    //进行高度的综合运算，透明我们的头像和ICON
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mLayMembers;
        if (view == null) return;

        if (verticalOffset == 0) {//完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
        } else {
            verticalOffset = Math.abs(verticalOffset);//取正
            //滚动的最高高度
            final int totalScroll = appBarLayout.getTotalScrollRange();
            if (verticalOffset >= totalScroll) {
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);
            } else {//中间状态
                float progress = 1 - verticalOffset / (float) totalScroll;
                Log.i("ChatUserFragment", progress + "");
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
            }
        }
    }

    @Override
    public void onInit(Group group) {
        mCollapsingBarLayout.setTitle(group.getName());
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.default_banner_group);
        options.centerCrop();
        Glide.with(this)
                .load(group.getPicture())
                .apply(options)
                .into(mHeader);

    }

    @Override
    public void onInitGroupMember(List<MemberUserModel> members, long moreCount) {
        if (members == null || members.size() == 0) return;
        // TODO: 2020/2/3 将头像布局抽离出去，Glide也得封装一下 
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (final MemberUserModel member : members) {
            //添加群成员头像
            ImageView p = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait, mLayMembers, false);
            mLayMembers.addView(p, 0);
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.default_portrait);
            options.centerCrop();
            options.dontAnimate();
            Glide.with(this)
                    .load(member.portrait)
                    .apply(options)
                    .into(p);
            p.setOnClickListener(v -> PersonalActivity.show(getContext(), member.userId));

        }
        if (moreCount > 0) {
            mMemberMore.setText(String.format("+%s", moreCount));
            mMemberMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2020/2/3 显示更多成员列表
                }
            });
        } else {
            mMemberMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAdminOption(boolean isAdmin) {
        if (isAdmin) {
            mToolbar.inflateMenu(R.menu.chat_group);
            mToolbar.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.action_add) {//加人按钮,mReceiverId就是群的id
                            GroupMemberActivity.showAdmin(getContext(), mReceiverId);
                            return true;
                        }
                        return false;
                    }
            );
        }
    }


}
