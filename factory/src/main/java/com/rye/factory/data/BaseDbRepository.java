package com.rye.factory.data;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.rye.catcher.factory.data.DbDataSource;
import com.rye.catcher.utils.CollectionUtil;
import com.rye.factory.data.helper.DbHelper;
import com.rye.factory.model.db.BaseDbModel;


import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 基本的数据库仓库
 * 实现对数据库的基本的监听操作
 * CreateBy ShuQin
 * at 2020/1/26
 */
// TODO: 2020/1/29    QueryTransaction.QueryResultListCallback<Data>--看看能否将这个抽离出去
public abstract class BaseDbRepository<Data extends BaseDbModel<Data>>
        implements DbDataSource<Data>,
        DbHelper.ChangeListener<Data>,
        QueryTransaction.QueryResultListCallback<Data> {
  private SucceedCallback<List<Data>> callback;

  protected final LinkedList<Data>  dataList =new LinkedList<>();

  private Class<Data> dataClass;//当前泛型真实的class信息

    public BaseDbRepository(){
        // TODO: 2020/1/26 ---通过反射获取当前类中泛型的Class信息这块我们可以自己封装一下，没必要引入三方库，虽然是老师的..
        //通过反射拿到当前类所有的泛型
        Type[] types= Reflector.getActualTypeArguments(BaseDbRepository.class,this.getClass());
        dataClass= (Class<Data>) types[0];
    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) {
       this.callback=callback;
       //添加数据监听器
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        this.callback=null;
        DbHelper.removeChangeListener(dataClass,this);
        dataList.clear();
    }
    //数据库统一通知的地方：增加/更改
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataSaved(Data... datas) {
        final boolean[] isChanged = {false};
        Arrays.asList(datas).stream().forEach(data -> {
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged[0] =true;
            }
        });
        if (isChanged[0]){
            notifyDataChanged();
        }
    }
    //数据库统一通知：删除
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataDeleted(Data... datas) {
        //在删除情况下进行删除操作
        final boolean[] isChanged = {false};
        Arrays.asList(datas).stream().forEach(data -> {
            if (dataList.remove(data)){
                isChanged[0] =true;
            }
        });
        //有数据变更才进行刷新
        if (isChanged[0]){
            notifyDataChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        if (tResult.size()==0){
            dataList.clear();
            notifyDataChanged();
            return;
        }
        Data[] datas= CollectionUtil.toArray(tResult,dataClass);
        onDataSaved(datas);
    }

    private void notifyDataChanged(){
        SucceedCallback<List<Data>> callback=this.callback;
        if (callback!=null){
            callback.onDataLoaded(dataList);
        }
    }


    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听操作
     */
    protected void registerDbChangedListener(){
        DbHelper.addChangeListener(dataClass,this);
    }


    protected void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {//下标大于0，说明是更新
            replace(index, data);
        } else {//下标为-1，说明是插入
            insert(data);
        }
    }

    protected void insert(Data data) {
        dataList.add(data);
    }

    private void replace(int index, Data data) {
        if (dataList.size() > 0) {//判断不用加，因为下标为-1，不会进入到此方法中
            dataList.remove(index);
            dataList.add(index, data);
        }
    }


    /**
     * 这里判断必须用isSame方法，用户名字或者其他不同的时候，
     * 只要id一样，就是同一条数据
     *
     * @param
     * @return
     */
    private int indexOf(Data data) {
        int index = -1;
        for (Data data1 : dataList) {
            index++;
            if (data1.isSame(data)) {
                return index;
            }
        }
        return -1;
    }


}
