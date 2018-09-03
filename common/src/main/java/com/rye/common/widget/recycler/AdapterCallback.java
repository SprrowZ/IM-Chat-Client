package com.rye.common.widget.recycler;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ZZG on 2018/8/31.
 */
public interface AdapterCallback<Data> {
   void updata(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
