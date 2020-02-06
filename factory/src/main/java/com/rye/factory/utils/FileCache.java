package com.rye.factory.utils;

import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.utils.HashUtil;
import com.rye.catcher.utils.StreamUtil;
import com.rye.factory.R;
import com.rye.factory.net.NetWork;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.channels.NetworkChannel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * CreateBy ShuQin
 * at 2020/2/5
 */
public class FileCache<Holder> {
    private File baseDir;//语音文件夹
    private String ext;//后缀名
    private CacheListener<Holder> listener;//下载监听

    //目标
    private SoftReference<Holder> holderSoftReference;


    public FileCache(String baseDir, String ext, CacheListener<Holder> listener) {
        this.baseDir = new File(zApplication.getCacheDirFile(), baseDir);
        this.ext = ext;
        this.listener = listener;
    }

    //构建一个缓存文件，同一个网络路径对应一个本地的文件
    private File buildCacheFile(String path) {
        String key = HashUtil.getMD5String(path);
        return new File(baseDir, key + "." + ext);
    }

    public void download(Holder holder, String path) {
        // 如果路径就是本地缓存路径，那么不需要下载
        if (path.startsWith(zApplication.getCacheDirFile().getAbsolutePath())) {
            listener.onDownloadSucceed(holder, new File(path));
            return;
        }
        //构建缓存文件
        final  File cacheFile=buildCacheFile(path);
        if (cacheFile.exists()&&cacheFile.length()>0){
            listener.onDownloadSucceed(holder,new File(cacheFile.getAbsolutePath()));
            return;
        }
        //把目标进行软引用
        holderSoftReference=new SoftReference<>(holder);
        OkHttpClient client= NetWork.getClient();
        Request request=new Request.Builder()
                .url(path)
                .get()
                .build();
        Call call=client.newCall(request);
        call.enqueue(new NetCallback(holder,cacheFile));

    }
    //拿最后的目标
    private Holder getLastHolderAndClear(){
        if (holderSoftReference==null){
            return null;
        }else{
            //拿到并清理软引用里的值

            Holder holder=holderSoftReference.get();
            holderSoftReference.clear();
            return holder;
        }
    }


    private class NetCallback implements Callback{
        private final SoftReference<Holder> holderSoftReference;
        private final File file;
        public NetCallback(Holder holder,File file){
            this.holderSoftReference=new SoftReference<Holder>(holder);
            this.file=file;
        }
        @Override
        public void onFailure(Call call, IOException e) {
               Holder holder=holderSoftReference.get();
               if (holder!=null&&holder==getLastHolderAndClear()){
                   FileCache.this.listener.onDownloadFailed(holder);
               }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream inputStream=response.body().byteStream();
            if (inputStream!=null && StreamUtil.copy(inputStream,file)){
                Holder holder=holderSoftReference.get();
                if (holder!=null && holder==getLastHolderAndClear()){
                    FileCache.this.listener.onDownloadSucceed(holder,file);
                }
            }

        }
    }

    public interface CacheListener<Holder> {
        //成功吧目标一块丢回去
        void onDownloadSucceed(Holder holder, File file);

        void onDownloadFailed(Holder holder);
    }

}
