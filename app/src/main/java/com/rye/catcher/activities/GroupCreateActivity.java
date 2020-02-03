package com.rye.catcher.activities;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rye.catcher.R;
import com.rye.catcher.common.app.PresenterToolbarActivity;

import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.common.widget.PortraitView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;

import com.rye.catcher.frags.media.GalleryFragment;
import com.rye.factory.persenter.group.GroupCreateContract;
import com.rye.factory.persenter.group.GroupCreatePresenter;
import com.yalantis.ucrop.UCrop;


import net.qiujuer.genius.ui.widget.EditText;


import java.io.File;


import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContract.Presenter>
        implements GroupCreateContract.View, GalleryFragment.OnSelectedListener {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private String mPortraitPath;

    private Adapter mAdapter;


    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
 
        mAdapter = new Adapter();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCreateClick() {
        hideSoftKeyBoard();
        String name = mName.getText().toString().trim();
        String desc = mDesc.getText().toString().trim();
        mPresenter.create(name, desc, mPortraitPath);

    }

    private void hideSoftKeyBoard() {
        View view = getCurrentFocus();
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected GroupCreateContract.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideDialogLoading();
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        hideSoftKeyBoard();
        new GalleryFragment().setListener(this)
                //show的时候建议使用getChildFragmentManager
                .show(getSupportFragmentManager(), GalleryFragment.class.getName());
    }

    @Override
    public void onSelectedImage(String path) {
        UCrop.Options options = new UCrop.Options();
        //压缩格式设置为PNG
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(96);
        //获取头像缓存地址
        // TODO: 2019/12/28 把头像的地址生成从Application中抽离处理。放到FileUtils中去
        File dPath = zApplication.getPortraitTmpFile();
        //设置属性
        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                .withAspectRatio(1, 1)
                .withMaxResultSize(520, 520)
                .withOptions(options)
                .start(this);
    }

    @Override
    public void onCreateSucceed() {
        hideDialogLoading();
        zApplication.showToast(R.string.label_group_create_succeed);
        finish();
    }


       class Adapter extends RecyclerAdapter<GroupCreateContract.ViewModel> {

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContract.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
        }
    }

      class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewModel> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.cb_select)
        CheckBox mSelect;

        @OnCheckedChanged(R.id.cb_select)
        void onCheckedChanged(boolean checked) {
            mPresenter.changeSelect(mData, checked);
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCreateContract.ViewModel viewModel) {
            mPortrait.setUp(Glide.with(GroupCreateActivity.this), viewModel.author);
            mName.setText(viewModel.author.getName());
            mSelect.setChecked(viewModel.isSelected);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从Activity传递过来的回调，然后取出其中的值进行图片加载
        //如果是我能处理的类型
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void loadPortrait(Uri uri) {

        mPortraitPath = uri.getPath();

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(this)
                .load(uri)
                .apply(options)
                .into(mPortrait);


    }
}
