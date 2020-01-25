package com.rye.catcher.factory.presenter;

import android.content.IntentFilter;

import androidx.recyclerview.widget.DiffUtil;

import com.rye.catcher.common.widget.recycler.RecyclerAdapter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * RecycleView的适配器
 * CreateBy ShuQin
 * at 2020/1/25
 */
public class BaseRecyclerPresenter<ViewMode, View extends BaseContract.RecyclerView>
        extends BasePresenter<View> {
    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    /**
     * 全量更新
     * @param dataList
     */
    protected void refreshData(final List<ViewMode> dataList) {
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if (view == null) return;
                RecyclerAdapter<ViewMode> adapter = view.getRecyclerAdapter();
                //整体替换数据，这是个全局刷新，replace中有notifyDataChanged
                adapter.replace(dataList);
                //通知上层数据更新，我觉得封装到RecycleAdapter中更好点
                view.onAdapterDataChanged();
            }
        });
    }
    /**
     * 差量更新 数据，刷新界面
     * @param diffResult
     * @param dataList
     */
    protected void refreshData(final DiffUtil.DiffResult diffResult, final List<ViewMode> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                refreshDataOnUiThread(diffResult, dataList);
            }
        });
    }

    /**
     * 差量更新数据，刷新界面
     * @param diffResult
     * @param dataList
     */
    private void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult, final List<ViewMode> dataList) {
        View view = getView();
        if (view == null) return;
        RecyclerAdapter<ViewMode> adapter = view.getRecyclerAdapter();
        //这里是清空后通过diffResult.dispatchUpdatesTo去差量更新，replace里有个notify是全量
        adapter.getItems().clear();
        adapter.getItems().addAll(dataList);
        //通知上层
        view.onAdapterDataChanged();
        //进行增量更新
        diffResult.dispatchUpdatesTo(adapter);
    }

}
