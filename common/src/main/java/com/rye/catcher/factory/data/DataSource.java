package com.rye.catcher.factory.data;

import androidx.annotation.StringRes;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public interface DataSource {
    /**
     * 同时继承两个接口，包含失败和成功的回调
     * @param <T>
     */
    interface  Callback<T> extends SucceedCallback<T>,FailedCallback{

    }

    /**
     * 只关注成功的回调
     * @param <T>
     */
    interface SucceedCallback<T>{
        void onDataLoaded(T t);
    }

    /**
     * 只关注失败的回调
     */
    interface  FailedCallback{
        void  onDataNotAvailable(@StringRes int res);
    }

    /**
     * 销毁操作
     */
    void dispose();
}
