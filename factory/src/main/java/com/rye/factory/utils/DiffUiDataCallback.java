package com.rye.factory.utils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/22
 */
public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>>
        extends DiffUtil.Callback {
   private List<T> mOldList,mNewList;

   public DiffUiDataCallback(List<T> oldList,List<T> newList){
       this.mNewList=newList;
       this.mOldList=oldList;
   }


    @Override
    public int getOldListSize() {
        //旧的数据大小
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        //新的数据大小
        return mNewList.size();
    }
     //两个数据是否相同，比如ID相等，其他不等，也可以通过判断是同一条数据
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld=mOldList.get(oldItemPosition);
        T beanNew=mNewList.get(newItemPosition);

        return beanNew.isSame(beanOld);
    }
     //在经过相等判断后，进一步判断是否有数据更改
    //比如，同一个用户的两个不同实例，其中的name字段不同
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
       T beanOld=mOldList.get(oldItemPosition);
       T beanNew=mNewList.get(newItemPosition);

       return beanNew.isUiContentSame(beanOld);
    }

    //进行比较的数据类型,泛型的目的是你和相同类型的数据进行比较
    public interface  UiDataDiffer<T>{
        //传递一个旧的数据，是否和标示的是同一条数据
        boolean isSame(T old);
        //和旧的数据对比，内容是否相同
        boolean isUiContentSame(T old);

    }
}
