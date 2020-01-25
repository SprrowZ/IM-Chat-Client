package com.rye.factory.data.helper;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;


import com.rye.factory.model.db.AppDatabase;
import com.rye.factory.model.db.Group;
import com.rye.factory.model.db.GroupMember;
import com.rye.factory.model.db.Group_Table;
import com.rye.factory.model.db.Message;
import com.rye.factory.model.db.Session;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CreateBy ShuQin
 * at 2020/1/24
 */
public class DbHelper {
    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    /**每一个表都有多个观察者
     *Class<?> 观察的表
     * Set<ChangedListener>:每一个表对应的观察者
     */
    private final Map<Class<?> , Set<ChangeListener>> changeListeners=new HashMap<>();

    /**
     * 从所有的监听器中获取某一个表的所有监听器
     * 为什么一个表会有多个监听器？会话是一条？消息是一条？每个会话都要有监听器~！
     * @param modelClass
     * @param <Model>
     * @return
     */
    private <Model extends BaseModel> Set<ChangeListener> getListeners(Class<Model> modelClass){
        if (changeListeners.containsKey(modelClass)){
            return changeListeners.get(modelClass);
        }
        return null;
    }
    /**
     * 添加监听者
     * @param mClass 对某个表进行关注
     * @param changeListener 监听者
     * @param <Model> 表的泛型
     */
    public static  <Model extends BaseModel> void  addChangeListener(final Class<Model> mClass,
                                                                     ChangeListener<Model> changeListener){
         Set<ChangeListener> changeListeners= instance.getListeners(mClass);
         if (changeListeners==null){
             changeListeners=new HashSet<>();
             //如果为空，添加进去的是个空的HashSet，底下还可以继续加数据
             instance.changeListeners.put(mClass,changeListeners);
         }
         changeListeners.add(changeListener);

    }

    /**
     * 删除监听器
     * @param mClass
     * @param changeListener
     * @param <Model>
     */
    public static  <Model extends BaseModel> void  removeChangeListener(final Class<Model> mClass,
                                                                     ChangeListener<Model> changeListener){
        Set<ChangeListener> changeListeners= instance.getListeners(mClass);
        if (changeListeners==null){
            //本身为空，说明没加过
            return;
        }
        changeListeners.remove(changeListener);

    }


    /**
     * 新增和修改的统一方法，还要通知！！界面必须及时刷新
     * @param mClass
     * @param models
     * @param <Model>
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <Model extends BaseModel> void save(final Class<Model> mClass, final Model... models) {
        if (models == null || models.length == 0) return;
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            Log.i("DbHelper","save..."+mClass.getName());
            ModelAdapter<Model> adapter = FlowManager.getModelAdapter(mClass);
            adapter.saveAll(Arrays.asList(models));
            //通知
            instance.notifySave(mClass,models);
        }).build().execute();
    }

    /**
     * 删除方法，还要通知！！界面必须及时刷新
     *
     * @param mClass
     * @param models
     * @param <Model>
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <Model extends BaseModel> void delete(final Class<Model> mClass, final Model... models) {
        if (models == null || models.length == 0) return;
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            ModelAdapter<Model> adapter = FlowManager.getModelAdapter(mClass);
            adapter.deleteAll(Arrays.asList(models));
            //通知
            instance.notifyDelete(mClass,models);
        });
    }

    /**
     *保存成功后进行通知
     * @param mClass
     * @param models
     * @param <Model>
     */
    // TODO: 2020/1/25 添加好友，流程会走两遍，待研究 
    @RequiresApi(api = Build.VERSION_CODES.N)
    private final  <Model extends BaseModel> void notifySave(final Class<Model> mClass, final Model... models) {
        // TODO: 2020/1/24
        Set<ChangeListener> listeners=getListeners(mClass);
        if (listeners!=null && listeners.size()>0){
            listeners.stream().forEach(listener->{
                listener.onDataSaved(models);
            });
        }
        //例外情况，跨表通知！！！
        //群成员变更，需要通知对应群信息更新
        //消息变化，通知会话列表更新
        if (GroupMember.class.equals(mClass)){
          updateGroup((GroupMember[]) models);
        }else  if (Message.class.equals(mClass)){
            updateSession((Message[])models);
        }
    }

    /**
     * 删除数据后进行通知
     * @param mClass
     * @param models
     * @param <Model>
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private final  <Model extends BaseModel> void notifyDelete(final Class<Model> mClass, final Model... models) {
        // TODO: 2020/1/24
        Set<ChangeListener> listeners=getListeners(mClass);
        if (listeners!=null && listeners.size()>0){
            listeners.stream().forEach(listener->{
                listener.onDataDeleted(models);
            });
        }

        //例外情况，跨表通知！！！
        //群成员变更，需要通知对应群信息更新
        //消息变化，通知会话列表更新
        if (GroupMember.class.equals(mClass)){
            updateGroup((GroupMember[]) models);
        }else  if (Message.class.equals(mClass)){
            updateSession((Message[])models);
        }
    }

    /**
     * 从成员中找出成员对应的群，并对群进行更新
     * @param members
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateGroup(GroupMember... members){
         //群唯一标志是其id
        final  Set<String> groupIds=new HashSet<>();
        Arrays.asList(members).stream().forEach(member->{
            groupIds.add(member.getGroup().getId());
        });
     DatabaseDefinition definition=FlowManager.getDatabase(AppDatabase.class);
     definition.beginTransactionAsync(databaseWrapper -> {
         List<Group> groups= SQLite.select()
                 .from(Group.class)
                 .where(Group_Table.id.in(groupIds))
                 .queryList();
          //通过群成员去通知群有更改
         instance.notifySave(Group.class,groups.toArray(new Group[0]));
     }).build().execute();



    }

    /**
     * 从消息列表中，筛选出对应的会话，并对会话进行更新
     * @param messages
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSession(Message... messages){
          final  Set<Session.Identify> identifies=new HashSet<>();
          Arrays.asList(messages).stream().forEach(message -> {
              Session.Identify identify=Session.createSessionIdentify(message);
              identifies.add(identify);
          });

        DatabaseDefinition definition=FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(databaseWrapper -> {
            // TODO: 2020/1/25 看看ModelAdapter的作用
            ModelAdapter<Session> adapter=FlowManager.getModelAdapter(Session.class);
            Session[] sessions=new Session[identifies.size()];
            //lambda表达式中，必须是final
            final int[] index = {0};
            identifies.stream().forEach(identify -> {
                Session session=SessionHelper.findFromLocal(identify.id);
                if (session==null){
                    session=new Session(identify);
                }
                //将最新消息刷新到当前会话中
                session.refreshToNow();
                //存储数据
                adapter.save(session);
                sessions[index[0]++]=session;
            });
            //会话全部通知
            instance.notifySave(Session.class,sessions);
        }).build().execute();
    }



  public   interface ChangeListener<Data>{
        void onDataSaved(Data... data);
        void onDataDeleted(Data... data);
   }
}
