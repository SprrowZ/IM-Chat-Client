package com.rye.common.widget;


import android.content.Context;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rye.common.R;
import com.rye.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * TODO: document your custom view class.
 */
public class GalleryView extends RecyclerView {

    //定义选中图片的最大数量
    private static final  int MAX_IMAGE_COUNT=3;
    //可显示的最小图片大小
    private static final  int MIN_IMAGE_FILE_SIZE=10*1024;
    //自己定义的一个Adapter
    private Adapter mAdapter=new Adapter();
    //Loader相关
    private LoaderCallback mLoaderCallback=new LoaderCallback();

    private static  final  int LOADER_ID=0x0100;

    //存储选中图片,这里LinkedList适合增删
    private List<Image> mSelectedImages = new LinkedList<>();

    private SelectedChangeListener mListener;
    public GalleryView(Context context) {
        super(context);
        init( );
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( );
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( );
    }

    private void init( ) {
      setLayoutManager(new GridLayoutManager(getContext(),4));
      setAdapter(mAdapter);
      mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
          @Override
          public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
              //Cell点击操作，如果说我们的点击是允许的，那么更新对应的Cell的状态
              //然后更新界面，同理；如果说不能允许点击（数量超了）那么就不刷新页面
              if (onItemSelectClick(image)){
                    holder.updataData(image);
                }
          }

      });
    }

    /**
     * 数据初始化的方法，Loader，用来加载数据
     * @param loaderManager Loader管理器
     *  返回一个LOADER_ID，可用于销毁Loader
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener){
        mListener=listener;
        loaderManager.initLoader(LOADER_ID,null,mLoaderCallback);
         return LOADER_ID;
    }

    /**
     * Cell点击的具体逻辑
     * @param image
     * @return Ture代表进行了数据更新，需要刷新，反之不需要。
     */
    private boolean onItemSelectClick(Image image){
        //是否需要进行刷新
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)){
            //如果之前在那么现在就移除，没毛病。。
            mSelectedImages.remove(image);
            image.isSelect = false;
            //状态已经改变则需要刷新
            notifyRefresh=true;
        }else{
            if (mSelectedImages.size()>=MAX_IMAGE_COUNT){
                //Toast,已经最大数量了。。。
                String str=getResources().getString(R.string.label_gallery_select_max_size);
                str=String.format(str,MAX_IMAGE_COUNT);
                Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
                notifyRefresh=false;
            }else{
                mSelectedImages.add(image);
                image.isSelect=true;
                notifyRefresh=true;
            }
        }
        //如果数据有修改，那么我们需要通知外面的监听者我们的数据改变了
        if (notifyRefresh){
            notifySelectedChanged();
        }
        return true;
    }

    /**
     * 得到选中图片的全部地址
     * @return 一个数组
     */
    public String[] getSelectedPath(){
        String[] paths=new String[mSelectedImages.size()];
        int index=0;
        for (Image image : mSelectedImages) {
            paths[index++]=image.path;
        }
        return paths;
    }

    /**
     * 可以进行清空选中的图片
     */
    public void clear(){
        //必须先重置状态，不然怎么刷新呢，
        // 其他没有选中的图片也有isSelect这个属性
        for (Image image : mSelectedImages) {
            image.isSelect=false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 通知外部选中状态改变，这里需要一个接口，供外部类实现，然后通过这个进行通知
     */
    private void notifySelectedChanged(){
        //得到监听者，并判断是否有监听者，进行回调
        SelectedChangeListener listener=mListener;
        if (listener!=null){
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    /**
     * 通知Adapter数据更新
     * @param list
     */
    private void updateSourceList(List<Image> list){
        mAdapter.replace(list);
    }


    /**
     * 用于实际的数据加载的Loader Callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>{

    private final   String[] IMAGE_PROJECTION=new String[]{
            MediaStore.Images.Media._ID,//Id
            MediaStore.Images.Media.DATA,//图片路径
            MediaStore.Images.Media.DATE_ADDED//图片的创建时间
    };

        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //创建一个Loader
            if (id==LOADER_ID){
                //如果是我们的ID，则可进行初始化
                return  new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2]+" DESC");//倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            //当Loader加载完成时
            List<Image> images=new ArrayList<>();
            //判断是否有数据
            if (data!=null){
                int count=data.getCount();
                if (count>0){
                    //移动游标到顶部
                    data.moveToFirst();
                    //得到对应的Index坐标
                    int indexId=data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath=data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate=data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);

                    do{
                        int id=data.getInt(indexId);
                        String path=data.getString(indexPath);
                        long dateTime=data.getLong(indexDate);
                        File file=new File(path);
                        if (!file.exists()||file.length()<MIN_IMAGE_FILE_SIZE){
                            //如果没有图片或者图片大小太小
                            continue;
                        }
                        Image image=new Image();
                        image.id=id;
                        image.path=path;
                        image.date=dateTime;
                        images.add(image);
                    }while (data.moveToNext());
                }

            }
            //更新数据
            updateSourceList(images);
        }

        @Override
        public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
            //Loader销毁或者重置了
            updateSourceList(null);
        }

    }



    /**
     * 数据接构
     */
    private static class Image{
      int id;//数据的ID
      long date;//图片的日期
        String path;//图片的路径，用来判断是不是同一张图片
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Image image = (Image) o;
            return Objects.equals(path, image.path);
        }

        @Override
        public int hashCode() {
            return 0;
        }

        boolean isSelect;//是否选中

    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {
        @Override
        protected int getItemViewType(int position, Image image) {
            //只有一种类型
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleryView.ViewHolder(root);
        }

    }

    /**
     * Cell对应的ViewHolder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image>{
          private ImageView mPic;
          private View mShade;
          private CheckBox mSelected;
        public ViewHolder(View itemView) {
            super(itemView);
            mPic=itemView.findViewById(R.id.im_image);
            mShade=itemView.findViewById(R.id.view_shade);
            mSelected=itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            RequestOptions options=new RequestOptions();
            options.centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.color.deep_purple_a200);
            Glide.with(getContext())
                  .load(image.path)
                  .apply(options)
                  .into(mPic);
            mShade.setVisibility(image.isSelect?VISIBLE:INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }

    /**
     * 对外的一个监听器
     */
    public interface SelectedChangeListener{
     void onSelectedCountChanged(int count);
    }
}
