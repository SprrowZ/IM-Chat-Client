package com.rye.catcher.frags.panel;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.rye.catcher.R;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.common.tools.AudioRecordHelper;
import com.rye.catcher.common.tools.UiTool;
import com.rye.catcher.common.widget.AudioRecordView;
import com.rye.catcher.common.widget.GalleryView;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.face.Face;
import com.rye.catcher.factory.presenter.BaseContract;

import net.qiujuer.genius.ui.Ui;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanelFragment extends BaseFragment {

    private View mFacePanel, mGalleryPanel, mRecordPanel;

    private PanelCallback mCallback;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_panel;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initFace(root);
        initRecord(root);
        initGallery(root);
    }

    public void setup(PanelCallback callback) {
        this.mCallback = callback;
    }

    private void initFace(View root) {
        mFacePanel = root.findViewById(R.id.lay_panel_face);
        final View facePanel = mFacePanel;
        View backspace = facePanel.findViewById(R.id.im_backspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
                PanelCallback callback = mCallback;
                if (callback == null) return;
                //模拟键盘操作 todo---zhege caozuo
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL,
                        0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);

                callback.getInputEditText().dispatchKeyEvent(event);
            }
        });
        //两者绑定，基本操作
        TabLayout tabLayout = (TabLayout) facePanel.findViewById(R.id.tab);
        ViewPager viewPager = facePanel.findViewById(R.id.pager);
        tabLayout.setupWithViewPager(viewPager);
        //一个表情显示48dp
        final int minFaceSize = (int) Ui.dipToPx(getResources(), 48);
        final int totalScreen = UiTool.getScreenWidth(getActivity());//拿到屏幕宽度
        final int spanCount = totalScreen / minFaceSize;//一行显示几个表情
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Face.all(getContext()).size();//需要几页
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                //初始化ViewPager的子布局
                LayoutInflater inflater = LayoutInflater.from(getContext());
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.lay_face_content, container, false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
                //设置Adapter
                List<Face.Bean> faces = Face.all(getContext()).get(position).faces;
                FaceAdapter adapter = new FaceAdapter(faces, new RecyclerAdapter.AdapterListenerImpl<Face.Bean>() {
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, Face.Bean bean) {
                        if (mCallback == null) return;
                        //表情添加到输入框
                        EditText editText = mCallback.getInputEditText();
                        Face.inputFace(getContext(), editText.getText(), bean, (int) (editText.getTextSize() + Ui.dipToPx(getResources(), 2)));

                    }
                });

                recyclerView.setAdapter(adapter);
                container.addView(recyclerView);

                return recyclerView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return Face.all(getContext()).get(position).name;//表情盘的描述
            }
        });


    }

    private void initRecord(View root) {
        View recordView = mRecordPanel = root.findViewById(R.id.lay_panel_record);
        final AudioRecordView audioRecordView = recordView.findViewById(R.id.view_audio_record);

        //录音的缓存文件
        File tmpFile = zApplication.getAudioTmpFile(true);
        //录音辅助工具类，很重要
        final AudioRecordHelper helper = new AudioRecordHelper(tmpFile, new AudioRecordHelper.RecordCallback() {
            @Override
            public void onRecordStart() {

            }

            @Override
            public void onProgress(long time) {//可以用力限制时长

            }

            @Override
            public void onRecordDone(File file, long time) {
                if (time < 1000) return;
                File audioFile = zApplication.getAudioTmpFile(false);//不是缓存文件
                if (file.renameTo(audioFile)) {
                    PanelCallback callback = mCallback;
                    if (callback != null) {//通知界面
                        callback.onRecordDone(audioFile, time);
                    }
                }
            }
        });


        audioRecordView.setup(new AudioRecordView.Callback() {
            @Override
            public void requestStartRecord() {
                //开始请求录音
                helper.recordAsync();
            }

            @Override
            public void requestStopRecord(int type) {
                //请求结束
                switch (type) {
                    case AudioRecordView.END_TYPE_CANCEL:
                    case AudioRecordView.END_TYPE_DELETE:
                        //这个是真的要暂停或者取消
                        helper.stop(true);
                        break;
                    case AudioRecordView.END_TYPE_NONE:
                    case AudioRecordView.END_TYPE_PLAY:
                        //播放中暂停就是想发送
                        helper.stop(false);
                }
            }
        });
    }

    private void initGallery(View root) {
        mGalleryPanel = root.findViewById(R.id.lay_gallery_panel);
        final View galleryPanel = mGalleryPanel;

        final GalleryView galleryView = galleryPanel.findViewById(R.id.view_gallery);
        final TextView selectedSize = galleryPanel.findViewById(R.id.txt_gallery_select_count);
        galleryView.setup(LoaderManager.getInstance(this), count -> {
            String resStr = getText(R.string.label_gallery_selected_size).toString();
            selectedSize.setText(String.format(resStr, count));
        });

        galleryPanel.findViewById(R.id.btn_send).setOnClickListener(v -> {
            //发送图片
            onGallerySendClick(galleryView, galleryView.getSelectedPath());

        });


    }

    private void onGallerySendClick(GalleryView galleryView, String[] paths) {
        //通知给聊天界面
        //清理状态
        galleryView.clear();

        PanelCallback callback = mCallback;
        if (callback == null) return;
        callback.onSendGallery(paths);


    }


    public void showFace() {
        mRecordPanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.GONE);
        mFacePanel.setVisibility(View.VISIBLE);
    }

    public void showRecord() {
        mRecordPanel.setVisibility(View.VISIBLE);
        mGalleryPanel.setVisibility(View.GONE);
        mFacePanel.setVisibility(View.GONE);
    }

    public void showGallery() {
        mRecordPanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.VISIBLE);
        mFacePanel.setVisibility(View.GONE);
    }

    //回调给聊天界面
    public interface PanelCallback {
        EditText getInputEditText();

        void onSendGallery(String[] paths);

        void onRecordDone(File file, long time);
    }

}
