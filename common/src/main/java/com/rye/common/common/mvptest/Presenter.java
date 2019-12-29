package com.rye.common.common.mvptest;

import android.text.TextUtils;

/**
 * CreateBy ShuQin
 * at 2019/12/25   ----然后在Activity实现IView接口，new 出Presenter进行数据操作。
 */
public class Presenter  implements  IPresenter{
    private IView mView;
    public Presenter(IView view){
      mView=view;
    }
    @Override
    public void submit() {
      String inputString=mView.getInputString();

      if (TextUtils.isEmpty(inputString)){
          return ;
      }
      int hashCode=inputString.hashCode();
      //和Model层交互
      IUserModel model=new UserModel();

      mView.setResultString(model.search(hashCode));
    }

    /**
     * 清空IView
     */
    public void detachView(){
        mView=null;
    }

}
