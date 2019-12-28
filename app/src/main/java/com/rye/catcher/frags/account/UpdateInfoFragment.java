package com.rye.catcher.frags.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rye.catcher.R;
import com.rye.catcher.frags.media.GalleryFragment;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.common.widget.PortraitView;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class UpdateInfoFragment extends BaseFragment implements GalleryFragment.OnSelectedListener {
    @BindView(R.id.im_portrait)
    PortraitView portraitView;
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        new GalleryFragment().setListener(this)
        //show的时候建议使用getChildFragmentManager
        .show(getChildFragmentManager(),GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从Activity传递过来的回调，然后取出其中的值进行图片加载
        //如果是我能处理的类型
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri!=null){
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * 加载Uri到当前的View中
     * @param uri
     */
    private void loadPortrait(Uri uri){
        RequestOptions options=new RequestOptions();
        options.centerCrop();
        Glide.with(getActivity())
                .load(uri)
                .apply(options)
                .into(portraitView);
    }

    @Override
    public void onSelectedImage(String path) {
        UCrop.Options options=new UCrop.Options();
        //压缩格式设置为PNG
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(96);
        //获取头像缓存地址
        // TODO: 2019/12/28 把头像的地址生成从Application中抽离处理。放到FileUtils中去
        File dPath= zApplication.getPortraitTmpFile();
        //设置属性
        UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(dPath))
                .withAspectRatio(1,1)
                .withMaxResultSize(520,520)
                .withOptions(options)
                .start(getActivity());
    }
}
