package com.rye.common.common.app;

import android.app.Application;
import android.os.SystemClock;

import android.widget.Toast;

import androidx.annotation.StringRes;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;

/**
 * Created at 2018/10/31.
 *
 */
public class zApplication extends Application {
   private static  zApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
         instance=this;
    }

//    /**
//     * 静态内部类创建单例模式
//     */
//  private static class innerApplicaion{
//        private static zApplication INSTANCE=new zApplication();
//  }
//  public static zApplication getInstance(){
//        return innerApplicaion.INSTANCE;
//  }
    /**
     * 外部获取单例
     *
     * @return Application
     */
    public static Application getInstance() {
        return instance;
    }
    /**
     * 得到缓存目录
     * @return
     */
  private static File  getCacheDirFile(){
      return getInstance().getCacheDir();
  }

   public static File getPortraitTmpFile(){
      //得到头像目录的缓存地址
       File dir=new File(getCacheDirFile()+"portrait");
       if (!dir.exists()){
           dir.mkdirs();
       }
       //删除其他头像文件
       File[] files=dir.listFiles();
       for (File file : files) {
           if (file.exists()){
               file.delete();
           }
       }
       //生成头像文件
       File path=new File(dir, SystemClock.uptimeMillis()+".jpg");
       return  path.getAbsoluteFile();
   }

    /**
     * 获取声音文件的本地地址--每次都不一样
     * @param isTmp 是否是缓存文件
     * @return
     */
   public static  File getAudioTmpFile(boolean isTmp){
      File dir=new File(getCacheDirFile(),"audio");
      dir.mkdirs();
      File[] files=dir.listFiles();
      if (files!=null && files.length>0){
          for (File file :files){
              file.delete();
          }
      }

      File path=new File(getCacheDirFile(),isTmp?"tmp.mp3":SystemClock.uptimeMillis()+".mp3");
      return path.getAbsoluteFile();

   }


    /**
     * 可能在非主线程中展示，需要做情景判断
     * 可以用Rxjava来实现，有很多实现方式就是了
     * @param msg
     */
   public static void showToast(final  String msg){
       //
       Run.onUiAsync(new Action() {
           @Override
           public void call() {
               Toast.makeText(instance,msg,Toast.LENGTH_SHORT).show();
           }
       });

   }


   public static void showToast(@StringRes int msgId){
       showToast(instance.getString(msgId));
   }


}
