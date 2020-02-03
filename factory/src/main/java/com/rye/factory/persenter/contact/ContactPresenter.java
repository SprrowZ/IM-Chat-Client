package com.rye.factory.persenter.contact;


import androidx.recyclerview.widget.DiffUtil;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.data.user.ContactDataSource;
import com.rye.factory.data.user.ContactRepository;
import com.rye.factory.model.db.User;
import com.rye.factory.persenter.BaseSourcePresenter;
import com.rye.factory.utils.DiffUiDataCallback;
import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/22
 */
public class ContactPresenter extends BaseSourcePresenter<User,User,ContactDataSource,ContactContract.View>
        implements ContactContract.Presenter , DataSource.SucceedCallback<List<User>> {

    public ContactPresenter(ContactContract.View view) {
        //初始化数据
        super(new ContactRepository(),view);
    }

    @Override
    public void start() {
        super.start();
        //本地数据已经交由父类处理
        //查询网络数据
        UserHelper.refreshContacts();
    }

    /**
     * 比较远端数据和本地数据的异同，很可能很耗时
     * @param newList
     * @param oldList
     */
    // TODO: 2020/1/22 待了解DiffUtil
    private void diff(List<User> newList,List<User> oldList){
        DiffUiDataCallback callback=new DiffUiDataCallback(oldList,newList);
        DiffUtil.DiffResult result=DiffUtil.calculateDiff(callback);
        //在对比完成后进行数据的赋值
        getView().getRecyclerAdapter().replace(newList);
        //尝试刷新界面
        result.dispatchUpdatesTo(getView().getRecyclerAdapter());
        getView().onAdapterDataChanged();
    }

    /**
     * 必须保证在子线程中，对比是一个耗时的操作
     * @param users
     */
    @Override
    public void onDataLoaded(List<User> users) {
        //无论怎么操作，数据变更最终都会通知到这里
        final  ContactContract.View view=getView();
        if (view==null) return;
        RecyclerAdapter<User> adapter=view.getRecyclerAdapter();
        //拿到老数据
        List<User> old=adapter.getItems();
        //新老数据对比
        DiffUtil.Callback callback=new DiffUiDataCallback<>(old,users);
        DiffUtil.DiffResult result=DiffUtil.calculateDiff(callback);
        //调用基类方法进行界面刷新
        refreshData(result,users);
    }

}
