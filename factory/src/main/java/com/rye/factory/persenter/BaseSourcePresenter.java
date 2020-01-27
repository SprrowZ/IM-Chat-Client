package com.rye.factory.persenter;

import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.data.DbDataSource;
import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.catcher.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/26
 * 数据操作基础的Presenter
 */
// TODO: 2020/1/26 ViewModel和Data应该是可以统一的 
public abstract class BaseSourcePresenter<Data,ViewModel,Source extends DbDataSource<Data>,
        View extends BaseContract.RecyclerView> extends BaseRecyclerPresenter<ViewModel,View>
implements DataSource.SuccessedCallback<List<Data>> {//回调交给上层处理，所以这里是个抽象类
    protected  Source mSource;
    public BaseSourcePresenter(Source source,View view) {
        super(view);
        this.mSource=source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource!=null){
            mSource.load(this);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource=null;
    }
}
