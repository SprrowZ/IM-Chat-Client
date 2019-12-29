package com.rye.catcher.frags.media;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rye.catcher.R;

import com.rye.common.common.tools.UiTool;
import com.rye.common.common.widget.GalleryView;


public class GalleryFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener {

    private GalleryView galleryView;
    private OnSelectedListener mListener;

    public GalleryFragment(){

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //先使用默认的,会导致状态栏也变灰色【Mix2上是没有这个问题的】
       // return new BottomSheetDialog(getContext());
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View root=inflater.inflate(R.layout.fragment_gallery,null,false);
           galleryView=root.findViewById(R.id.galleryView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        galleryView.setup(LoaderManager.getInstance(this),this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
      //如果选中的一张图片
        if (count>0){
            //隐藏自己
            dismiss();
        }
        if (mListener!=null){
            //得到所有的选中的图片的路径
            String[] paths=galleryView.getSelectedPath();
            //返回第一张
            mListener.onSelectedImage(paths[0]);
            //取消和唤起者之间的应用，加快内存回收
            mListener=null;
        }
    }

    /**
     * 设置事件监听，并返回自己
     * @param listener
     * @return
     */
    public GalleryFragment setListener(OnSelectedListener listener){
        mListener=listener;
        return this;
    }

    /**
     * 选中图片的监听器
     */
    public interface  OnSelectedListener{
       void onSelectedImage(String path);
    }


    private static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window=getWindow();
            if (window==null)
                return;

            //得到屏幕高度
            int screenHeight=getContext().getResources().getDisplayMetrics().heightPixels;
            //得到状态栏高度，一般是25dp
//            int statusHeight= (int) Ui.dipToPx(getContext().getResources(),25);
            int statusHeight= UiTool.getStatusBarHeight(getOwnerActivity());
            int dialogHeight=screenHeight-statusHeight;

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight<=0?ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight);

        }
    }

}
