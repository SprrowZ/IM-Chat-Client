package com.rye.factory.persenter.message;

import com.rye.catcher.factory.presenter.BaseContract;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.User;
import com.rye.factory.model.db.view.MemberUserModel;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/1/27
 * 聊天契约类
 */
public interface ChatContract {
    interface  Presenter extends BaseContract.Presenter{
        // TODO: 2020/1/27 ---能否将三个方法合并成一个，将处理逻辑抛给上层？闲的你？
     void pushText(String content);
     void pushAudio(String path,long time);
     void pushImages(String[] paths);
     
     boolean rePush(Message message);
    }
    //人、群初始化界面不同，所以需要传入泛型
    interface View<ViewModel> extends BaseContract.RecyclerView<Presenter,Message>{
        void  onInit(ViewModel model);
    }
    //人聊天的界面
    interface  UserView extends View<User>{

    }
    //群聊天的界面
    interface GroupView extends View<Group>{
        void showAdminOption(boolean isAdmin);//管理员显示添加人员的按钮
        void onInitGroupMember(List<MemberUserModel> members,long moreCount);
    }
}
