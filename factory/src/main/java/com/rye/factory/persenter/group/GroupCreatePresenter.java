package com.rye.factory.persenter.group;


import android.text.TextUtils;

import com.rye.catcher.factory.data.DataSource;
import com.rye.catcher.factory.presenter.BaseRecyclerPresenter;
import com.rye.factory.Factory;
import com.rye.factory.R;
import com.rye.factory.data.helper.GroupHelper;
import com.rye.factory.data.helper.UserHelper;
import com.rye.factory.model.api.group.GroupCreateModel;
import com.rye.factory.model.card.GroupCard;
import com.rye.factory.model.db.view.UserSampleModel;
import com.rye.factory.net.UpLoadHelper;

import net.qiujuer.genius.kit.handler.Run;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 群创建的Presenter
 * CreateBy ShuQin
 * at 2020/1/31
 */
public class GroupCreatePresenter extends BaseRecyclerPresenter<GroupCreateContract.ViewModel,
        GroupCreateContract.View>
implements GroupCreateContract.Presenter,DataSource.Callback<GroupCard>{
    private Set<String> users=new HashSet<>();

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        Factory.runOnAsync(loader);
    }

    @Override
    public void create(String name, String desc, String picture) {
          GroupCreateContract.View view=getView();
          view.showLoading();
          //判断参数
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(desc)
                ||TextUtils.isEmpty(picture)||users.size()==0){
            view.showError(R.string.label_group_create_invalid);
            return;
        }
        //上传图片
        Factory.runOnAsync(() -> {
            String url=uploadPicture(picture);
            if (TextUtils.isEmpty(url))
                return;
            //创建群
            GroupCreateModel model=new GroupCreateModel(name,desc,url,users);
            GroupHelper.create(model,GroupCreatePresenter.this);
        });

        //请求接口

        //处理回调
    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
          if (isSelected){
              users.add(model.author.getId());
          }else {
              users.remove(model.author.getId());
          }
    }


    private String uploadPicture(String path){
        String url= UpLoadHelper.uploadPortrait(path);
        if (TextUtils.isEmpty(url)){
            Run.onUiAsync(() -> {
                GroupCreateContract.View view=getView();
                if (view!=null){
                    view.showError(R.string.data_upload_error);
                }
            });
        }
        return url;
    }

    /**
     * 加载RecycleView数据
     */
    private Runnable loader= () -> {
        List<UserSampleModel> sampleModels= UserHelper.getSampleontact();
        List<GroupCreateContract.ViewModel> models=new ArrayList<>();
        for (UserSampleModel sampleModel:sampleModels){
            GroupCreateContract.ViewModel viewModel=new GroupCreateContract.ViewModel();
            viewModel.author=sampleModel;
            models.add(viewModel);
        }
        //刷新界面
        refreshData(models);
    };

    @Override
    public void onDataLoaded(GroupCard groupCard) {
        Run.onUiAsync(() -> {
            GroupCreateContract.View view=getView();
            if (view!=null){
                view.onCreateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(int res) {
        Run.onUiAsync(() -> {
            GroupCreateContract.View view=getView();
            if (view!=null){
                view.showError(res);
            }
        });
    }
}
