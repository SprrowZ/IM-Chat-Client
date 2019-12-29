package com.rye.common.common.widget.recycler;

/**
 * Created
 */
public interface AdapterCallback<Data> {
   void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
