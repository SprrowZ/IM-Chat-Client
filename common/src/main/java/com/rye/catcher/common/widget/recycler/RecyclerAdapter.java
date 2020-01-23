package com.rye.catcher.common.widget.recycler;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rye.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by ZZG on 2018/8/31.
 */
public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {

    private List<Data> mDataList = new ArrayList<>();
    private AdapterListener<Data> mListener;

    /**
     * 构造函数
     */
    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<Data> listener) {

        this(new ArrayList<Data>(), listener);
    }

    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDataList = dataList;
        this.mListener = listener;
    }

    /**
     * 创建一个ViewHolder
     *
     * @param parent   RecyclerView
     * @param viewType 界面的类型--->约定viewType是XML布局Id
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View root = inflater.inflate(viewType, parent, false);
        //通过子类必须实现的方法，得到一个viewHolder,viewType强制指定为xml的id
        ViewHolder<Data> holder = onCreateViewHolder(root, viewType);

        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        //将view绑定viewHolder，就可以通过tag取到viewHolder
        //设置View的Tag为ViewHolder，进行双向绑定
        root.setTag(R.id.tag_recycler_holder,holder);

        //--------------------只是将view绑定到holder中，不必非得在构造函数中
        holder.unbinder = ButterKnife.bind(holder, root);//将holder绑定到根布局

        holder.callback = this;
        return holder;
    }


    /**
     * 绑定数据到一个Holder上
     *
     * @param holder   viewholder
     * @param position 坐标
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Data> holder, int position) {
        //得到需要绑定的数据
        Data data = mDataList.get(position);
        //触发holder的绑定方法
        holder.bind(data);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    /**
     * 得到布局的类型
     *
     * @param position 坐标
     * @param data     当前的数据
     * @return XML文件的Id，用于创建viewHolder
     */
    @LayoutRes
    protected abstract int getItemViewType(int position, Data data);


    /**
     * 得到一个新的ViewHolder
     *
     * @param root     根布局
     * @param viewType 布局类型，实际上就是XML文件的ID
     * @return
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);





    public void add(Data data) {
        mDataList.add(data);
        //只需要更新插入
        notifyItemInserted(mDataList.size() - 1);
        //notifyDataSetChanged();这个是更新整个列表
    }

    /**
     * ViewHolder内部数据更新
     * @param data
     * @param holder
     */
    @Override
    public void update(Data data, ViewHolder<Data> holder) {
        //得到当前ViewHolder的坐标
        int pos=holder.getAdapterPosition();
        if (pos>=0){
            //进行数据的移除与更新
            mDataList.remove(pos);
            mDataList.add(pos,data);
            //通知数据更新
            notifyItemChanged(pos);
        }
    }

    /**
     * 插入一堆数据，并通知这段集合更新
     *
     * @param dataList
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPos, dataList.length);
        }
    }

    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeChanged(startPos, dataList.size());
        }
    }

    /**
     * 删除操作
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 返回数据集合
     * @return
     */
    public List<Data> getItems(){
        return mDataList;
    }
    /**
     * 替换为一个新的集合，其中包括了清空
     *
     * @param dataList 一个新的集合
     */
    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0)
            return;
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    /**
     * 对点击和长按事件进行处理
     */
    @Override
    public void onClick(View v) {
        //跟上面的setTag相对应
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            //得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            //得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemLongClick(viewHolder, mDataList.get(pos));
            return true;
        }
        return false;
    }

    /**
     * 设置适配器的监听
     */
    public void setListener(AdapterListener<Data> adapterListener) {
        this.mListener = adapterListener;
    }

    /**
     * 自定义监听器
     *
     * @param <Data> 泛型
     */
    public interface AdapterListener<Data> {
        void onItemClick(RecyclerAdapter.ViewHolder holder, Data data);

        void onItemLongClick(RecyclerAdapter.ViewHolder holder, Data data);
    }

    /**
     * 自定义的viewHolder
     *
     * @param <Data> 泛型类型
     */
    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {

        private Unbinder unbinder;
        //存贮callback
        private AdapterCallback callback;
        protected Data mData;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

        /**
         * 当触发绑定数据的时候的回调，必须复写
         *
         * @param data 绑定的数据
         */
        protected abstract void onBind(Data data);

        /**
         * holder自己对自己对应的Data进行更新
         *
         * @param data
         */
        public void updataData(Data data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }

    /**
     * 如果不用类继承接口实现的话，那么setAdapterListener的时候，就必须实现两个方法
     * @param <Data>
     */
    public  static abstract class AdapterListenerImpl<Data> implements AdapterListener<Data>{
        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, Data data) {

        }
    }

}
