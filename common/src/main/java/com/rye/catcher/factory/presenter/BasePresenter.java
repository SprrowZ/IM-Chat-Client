package com.rye.catcher.factory.presenter;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public abstract class BasePresenter<T extends BaseContract.View>  implements BaseContract.Presenter {
    private T mView;
    public BasePresenter(T  view){
        setView(view);
    }
    protected  void setView(T view){
        this.mView=view;
        this.mView.setPresenter(this);//绑定不要忘记
    }
    protected  final T getView(){
        return mView;
    }
    @Override
    public void start() {
       T view=mView;
       if (view!=null){
           view.showLoading();
       }
    }
    @Override
    public void destroy() {
        T view=mView;
        mView=null;
        if (view!=null){//防止内存泄漏
            view.setPresenter(null);
        }
    }


}
