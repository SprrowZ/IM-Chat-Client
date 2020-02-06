package com.rye.catcher.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.ArrayMap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.rye.catcher.utils.StreamUtil;
import com.rye.common.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * CreateBy ShuQin
 * at 2020/2/3
 */
public class Face {
    //存储了每一个表情
    private static final ArrayMap<String, Bean> FACE_MAP = new ArrayMap<>();
    private static List<FaceTab> FACE_TABS = null;

    /**
     * 初始化资源文件
     * @param context
     */
    private static void init(Context context) {
        if (FACE_TABS == null) {
            ArrayList<FaceTab> faceTabs = new ArrayList<>();
            FaceTab tab = initAssetsface(context);
            if (tab != null) {
                faceTabs.add(tab);
            }
            tab = initResourceFace(context);
            if (tab != null) {
                faceTabs.add(tab);
            }
            //init map
            for (FaceTab faceTab : faceTabs) {
                faceTab.copyToMap(FACE_MAP);
            }
            // TODO: 2020/2/4 深入了解Collections 
            FACE_TABS = Collections.unmodifiableList(faceTabs);

        }
    }

    /**
     * 从face-t.zip包解析我们的表情
     * @param context
     * @return
     */
    private static FaceTab initAssetsface(Context context) {
        String faceAsset = "face-t.zip";
        //路径：data/data/包名/files/face/ft/*
        String faceCacheDir = String.format("%s/face/tf", context.getFilesDir());
        File faceFolder = new File(faceCacheDir);

        if (!faceFolder.exists()) {
            //不存在进行初始化
            if (faceFolder.mkdirs()) {
                try {
                    // TODO: 2020/2/4 资源文件改为服务端端下发
                    //将Assets文件拷贝到本地路径下，实际上，这个应该由服务端下发
                    InputStream inputStream = context.getAssets().open(faceAsset);
                    File faceSource = new File(faceFolder, "source.zip");
                    StreamUtil.copy(inputStream, faceSource);
                    //文件解压
                    unZipFile(faceSource, faceFolder);
                    //删除zip文件
                    StreamUtil.delete(faceSource.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //解压json文件
        File infoFile = new File(faceCacheDir, "info.json");

        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = gson.newJsonReader(new FileReader(infoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        //解析
        FaceTab tab = gson.fromJson(reader, FaceTab.class);
        for (Bean face : tab.faces) {//相对路径改为绝对路径
            face.preview = String.format("%s/%s", faceCacheDir, face.preview);
            face.source = String.format("%s/%s", faceCacheDir, face.source);
        }
        return tab;
    }

    /**
     * 解压zip操作，可以抽离出去
     *
     * @param zipFile
     * @param desDir
     * @throws IOException
     */
    private static void unZipFile(File zipFile, File desDir) throws IOException {
        final String folderPath = desDir.getAbsolutePath();
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            //过滤掉缓存的文件
            String name = entry.getName();
            if (name.startsWith(".")) continue;
            //输入流
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + name;
            //防止名字错乱
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            //输出文件
            StreamUtil.copy(in, desFile);

        }
    }

    //从drawable中加载数据并映射到对应的key
    private static FaceTab initResourceFace(Context context) {
        final ArrayList<Bean> faces = new ArrayList<>();
        final Resources resources = context.getResources();
        String packageName = context.getApplicationInfo().packageName;
        for (int i = 0; i <= 142; i++) {//从drawable中取出资源
            //i==1--->001
            String key = String.format(Locale.ENGLISH, "fb%03d", i);
            String resStr = String.format(Locale.ENGLISH, "face_base_%03d", i);
            int resId = resources.getIdentifier(resStr, "drawable", packageName);
            if (resId == 0) continue;
            faces.add(new Bean(key, resId));

        }
        if (faces.size() == 0) return null;
        return new FaceTab(faces, "NAME", faces.get(0).preview);
    }


    //获取所有的表情
    public static List<FaceTab> all(@NonNull Context context) {
        init(context);
        return FACE_TABS;
    }

    //输入表情到editable中
    public static void inputFace(@NonNull final Context context, final Editable editable, final Face.Bean bean,
                                 final int size) {
        // TODO: 2020/2/4 替换过时Api
        Glide.with(context)
                .asBitmap()
                .load(bean.preview)
                .into(new SimpleTarget<Bitmap>(size,size) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                         Spannable spannable=new SpannableString(String.format("[%s]",bean.key));
                         ImageSpan span=new ImageSpan(context,resource,ImageSpan.ALIGN_BASELINE);
                         spannable.setSpan(span,0,spannable.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                         editable.append(spannable);
                    }
                });

    }

    // 拿一个Bean
    // key: ft001
    public static Bean get(Context context, String key) {
        init(context);
        if (FACE_MAP.containsKey(key)) {
            return FACE_MAP.get(key);
        }
        return null;
    }

    //从Spannable中解析表情并替换显示
    public static Spannable decode(@NonNull View target, final Spannable spannable, final int size) {
        if (spannable == null)
            return null;
        String str = spannable.toString();
        if (TextUtils.isEmpty(str))
            return null;
        final Context context = target.getContext();
        // 进行正在匹配[][][]
        Pattern pattern = Pattern.compile("(\\[[^\\[\\]:\\s\\n]+\\])");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            // [ft112]
            String key = matcher.group();
            if (TextUtils.isEmpty(key))
                continue;

            Bean bean = get(context, key.replace("[", "").replace("]", ""));
            if (bean == null)
                continue;

            final int start = matcher.start();
            final int end = matcher.end();

            // 得到一个复写后的标示
            ImageSpan span = new FaceSpan(context, target, bean.source, size);

            // 设置标示
            spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }


        return spannable;
    }

    /**
     * 代表一个表情盘，含有很多表情
     */
    public static class FaceTab {


        public List<Bean> faces = new ArrayList<>();
        public String name;
        //预览图，包括了drawable下面的资源int类
        public Object preview;

        public FaceTab(ArrayList<Bean> faces, String name, Object preview) {
            this.faces = faces;
            this.name = name;
            this.preview = preview;
        }

        //添加到Map中
        public void copyToMap(ArrayMap<String, Bean> faceMap) {
            for (Bean face : faces) {
                faceMap.put(face.key, face);
            }
        }
    }

    public static class Bean {
         Bean(String key, int preview) {
            this.key = key;
            this.source = preview;
            this.preview = preview;
        }

        public   String key;
        public   String desc;
        public   Object source;
        public   Object preview;
    }

    // 表情标示
    public static class FaceSpan extends ImageSpan {
        // 自己真实绘制的
        private Drawable mDrawable;
        private View mView;
        private int mSize;

        // TODO: 2020/2/4 替换掉这个复杂的实现
        /**
         * 构造
         *  
         * @param context 上下文
         * @param view    目标View，用于加载完成时刷新使用
         * @param source  加载目标
         * @param size    图片的显示大小
         */
        public FaceSpan(Context context, View view, Object source, final int size) {
            // 虽然设置了默认的表情，但是并不显示，只是用于占位
            super(context, R.drawable.default_face, ALIGN_BOTTOM);
            this.mView = view;
            this.mSize = size;
            RequestOptions options=new RequestOptions();
            options.fitCenter();
            Glide.with(context)
                    .asGif()
                    .load(source)
                    .apply(options)
                    .into(new SimpleTarget<GifDrawable>() {
                        @Override
                        public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                            mDrawable = resource.getCurrent();
                            // 获取自测量高宽
                            int width = mDrawable.getIntrinsicWidth();
                            int height = mDrawable.getIntrinsicHeight();
                            // 设置进去
                            mDrawable.setBounds(0, 0, width > 0 ? width : size,
                                    height > 0 ? height : size);

                            // 通知刷新
                            mView.invalidate();
                        }
                    });
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            // 走我们自己的逻辑，进行测量
            Rect rect = mDrawable != null ? mDrawable.getBounds() :
                    new Rect(0, 0, mSize, mSize);

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            return rect.right;
        }

        @Override
        public Drawable getDrawable() {
            // 复写拿Drawable的方法，当然这里有可能返回的是null
            return mDrawable;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            // 增加判断
            if (mDrawable != null)
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }
}
