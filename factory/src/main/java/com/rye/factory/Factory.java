package com.rye.factory;

import android.app.Application;

import com.rye.common.common.app.zApplication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CreateBy ShuQin
 * at 2019/12/29
 */
public class Factory {
   private static final Factory instance;

    private final Executor executor;

    static {
        instance=new Factory();
    }

    private Factory(){
        executor= Executors.newFixedThreadPool(4);
    }

    /**
     * 返回全局的Application
     * @return
     */
    public static Application app(){
        return zApplication.getInstance();
    }

    /**
     * 异步运行的方法
     * @param runnable
     */
    public static void runOnAsync(Runnable runnable){
        //拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

}
