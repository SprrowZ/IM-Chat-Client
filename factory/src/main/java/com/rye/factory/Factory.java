package com.rye.factory;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.rye.catcher.common.app.zApplication;
import com.rye.catcher.factory.data.DataSource;
import com.rye.factory.data.group.GroupCenter;
import com.rye.factory.data.group.GroupDispatcher;
import com.rye.factory.data.message.MessageCenter;
import com.rye.factory.data.message.MessageDispatcher;
import com.rye.factory.data.user.UserCenter;
import com.rye.factory.data.user.UserDispatcher;
import com.rye.factory.model.api.PushModel;
import com.rye.factory.model.api.RspModel;
import com.rye.factory.model.card.GroupCard;
import com.rye.factory.model.card.GroupMemberCard;
import com.rye.factory.model.card.MessageCard;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.persistence.Account;
import com.rye.factory.utils.DBFlowExclusionStrategy;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CreateBy ShuQin
 * at 2019/12/29
 */
public class Factory {
    private static final String TAG = Factory.class.getName();
    private static final Factory instance;
    //全局线程池
    private final Executor executor;

    private final Gson gson;

    static {
        instance = new Factory();
    }

    private Factory() {
        executor = Executors.newFixedThreadPool(4);

        gson = new GsonBuilder()
                //时间格式和服务器保持一直
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setExclusionStrategies(new DBFlowExclusionStrategy())//过滤级别
                .create();
    }

    /**
     * Factory中的持久化---在Application调用调用
     */
    public static void setup() {
        //初始化DBFlow数据库
        FlowManager.init(new FlowConfig.Builder(app())
                .openDatabasesOnInit(true)
                .build());


        //持久化的数据
        Account.load(app());
    }


    /**
     * 返回全局的Application
     *
     * @return
     */
    public static Application app() {
        return zApplication.getInstance();
    }

    /**
     * 异步运行的方法
     *
     * @param runnable
     */
    public static void runOnAsync(Runnable runnable) {
        //拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

    /**
     * 返回一个全局的Gson，进行全局的Gson初始化
     *
     * @return
     */
    public static Gson getGson() {
        return instance.gson;
    }


    /**
     * 解析错误Code，把服务端自定义的错误Code解析成我们对应的String
     *
     * @param model
     * @param callback
     */
    public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback) {
        if (model == null)
            return;
        switch (model.getCode()) {
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                zApplication.showToast(R.string.data_rsp_error_account_token);
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;

        }
    }

    private static void decodeRspCode(@StringRes final int resId,
                                      final DataSource.FailedCallback callback) {
        if (callback != null) {
            callback.onDataNotAvailable(resId);
        }
    }

    /**
     * 收到账户退出的信息需要退出账户重新登陆
     */
    private void logout() {

    }

    /**
     * 处理推送来的消息
     *
     * @param message
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void dispatchPush(String message) {
        if (!Account.isLogined()) return;
        //解析推送过来的数据
        PushModel model = PushModel.decode(message);
        if (model == null) return;
        Log.e(TAG, model.toString());

        model.getEntities().stream().forEach(entity -> {
            Log.e(TAG, entity.toString());
            switch (entity.type) {
                case PushModel.ENTITY_TYPE_LOGOUT:
                    //退出命令直接退出该方法
                    return;
                case PushModel.ENTITY_TYPE_MESSAGE: {
                    MessageCard card = getGson().fromJson(entity.content, MessageCard.class);
                    getMessageCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_FRIEND: {
                    UserCard card = getGson().fromJson(entity.content, UserCard.class);
                    getUserCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP: {
                    GroupCard card = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS: {
                    //群成员变更，回来的是一个群成员的列表
                    // TODO: 2020/1/26 待了解Gson解析列表
                    Type type = new TypeToken<List<GroupMemberCard>>() {
                    }.getType();
                    List<GroupMemberCard> card = getGson().fromJson(entity.content, type);
                    getGroupCenter().dispatch(card.toArray(new GroupMemberCard[0]));
                    break;
                }
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS:
                    // TODO: 2020/1/26
                    break;
            }
        });


    }

    /**
     * 获取一个用户中心的实现类
     *
     * @return
     */
    public static UserCenter getUserCenter() {
        return UserDispatcher.instance();
    }

    /**
     * 获取一个消息中心的实现类
     *
     * @return
     */
    public static MessageCenter getMessageCenter() {
        return MessageDispatcher.instance();
    }


    /**
     * 获取一个群中心的实现类
     *
     * @return
     */
    public static GroupCenter getGroupCenter() {
        return GroupDispatcher.instance();
    }
}
