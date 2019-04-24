package com.rye.common.widget.recycler;

/**
 * Created by ZZG on 2018/8/31.
 */
public interface AdapterCallback<Data> {
   void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
