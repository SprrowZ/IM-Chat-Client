package com.rye.common.app;

import android.app.Application;
import android.os.SystemClock;

import java.io.File;

/**
 * Created at 2018/10/31.
 *
 * @author Zzg
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

}
