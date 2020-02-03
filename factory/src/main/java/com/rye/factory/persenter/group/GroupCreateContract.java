package com.rye.factory.persenter.group;

import com.rye.catcher.factory.model.Author;
import com.rye.catcher.factory.presenter.BaseContract;

/**
 * CreateBy ShuQin
 * at 2020/1/31
 */
public interface GroupCreateContract {
    interface Presenter extends BaseContract.Presenter{
        void create(String name,String desc,String picture);
        //更改选中状态
        void changeSelect(ViewModel model,boolean isSelected);
    }

    interface View extends BaseContract.RecyclerView<Presenter,ViewModel>{
          void onCreateSucceed();
    }
    class ViewModel{
       public Author author;
       public  boolean isSelected;
    }
}
