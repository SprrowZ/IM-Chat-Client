package com.rye.common.widget.recycler;

/**
 * Created
 */
public interface AdapterCallback<Data> {
   void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
