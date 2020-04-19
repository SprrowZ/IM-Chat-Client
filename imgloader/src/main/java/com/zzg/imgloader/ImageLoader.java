package com.zzg.imgloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ImageLoader {

    public static void displayImage(ImageView view, String url){
        Glide.with(view.getContext())
        .load(url)
        .apply(initCommonRequestOption())
        .into(view);
    }

    public static void displayImage(ImageView view, Uri uri){
        Glide.with(view.getContext())
                .load(uri)
                .apply(initCommonRequestOption())
                .into(view);
    }

    public static void displayImage(ImageView view, @DrawableRes int resourceId){
        Glide.with(view.getContext())
                .load(resourceId)
                .apply(initCommonRequestOption())
                .into(view);
    }

    public static void display(Context context, @DrawableRes int resourceId, CustomViewTarget target) {
        Glide.with(context)
                .load(resourceId)
                .apply(initCommonRequestOption())
                .into(target);
        //初始化背景
   }

    /**
     * 高斯模糊
     * @param view
     * @param uri
     */
   private static void displayBlur(ImageView view , Uri uri){
        Glide.with(view.getContext())
                .load(uri)
                .apply(initBlurReqOpt(view.getContext()))
                .into(view);
   }

    /**
     * 为非View加载图片
     * @param context
     * @param target
     * @param url
     */
    private void displayImageForTarget(Context context, Target target, String url){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())
                .into(target);
    }





    /**
     * 公共配置
     * @return
     */
    private static RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }

    /**
     * 高斯模糊
     * @param context
     * @return
     */
    private static RequestOptions initBlurReqOpt(Context context) {
        RequestOptions options = initCommonRequestOption();
        options.transform(new BlurTransformation(context));
        return options;
    }
}
