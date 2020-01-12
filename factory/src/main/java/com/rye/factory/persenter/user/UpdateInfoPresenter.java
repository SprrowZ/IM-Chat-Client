package com.rye.factory.persenter.user;

import android.text.TextUtils;
import android.widget.TextView;

import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BasePresenter;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.model.api.user.UserUpdateModel;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.User;
import com.rye.factory.net.UpLoadHelper;
import com.rye.factory.persenter.account.LoginContract;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * CreateBy ShuQin
 * at 2020/1/12
 */
public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
        implements UpdateInfoContract.Presenter, DataSource.Callback<UserCard> {
    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc,final boolean isMan) {
           start();
           final UpdateInfoContract.View view=getView();
           if (TextUtils.isEmpty(photoFilePath)||TextUtils.isEmpty(desc)){
               view.showError(R.string.data_account_update_invalid_parameter);
           }else{
               Factory.runOnAsync(new Runnable() {
                   @Override
                   public void run() {
                        String url= UpLoadHelper.uploadPortrait(photoFilePath);
                        if (TextUtils.isEmpty(url)){
                            view.showError(R.string.data_upload_error);
                        }else{
                            //构建model
                            UserUpdateModel model=new UserUpdateModel("",url,desc,
                                    isMan? User.SEX_MAN:User.SEX_WOMAN);
                            //进行网络请求上传
                            UserHelper.update(model,UpdateInfoPresenter.this);
                        }
                   }
               });
           }
    }

    @Override
    public void onDataLoaded(UserCard userCard) {
        final UpdateInfoContract.View view = getView();
        if (view == null) {
            return;
        }
        //并不保证是主线程，需要进行线程切换
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                //调用主界面注册成功
                view.updateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int res) {
        final  UpdateInfoContract.View view=getView();
        if (view==null){
            return;
        }
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(res);
            }
        });
    }
}
