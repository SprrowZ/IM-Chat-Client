package com.rye.factory.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.rye.factory.Factory;
import com.rye.factory.model.api.account.AccountRspModel;
import com.rye.factory.model.db.User;
import com.rye.factory.model.db.User_Table;

/**
 * CreateBy ShuQin
 * at 2020/1/11
 * 保存用户的一些关键性信息
 */

public class Account {

    private static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_TOKEN= "KEY_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";


    //设备ID是否已经绑定到了服务器
    private static boolean isBind;
    //一些关键性信息
    //绑定的设备ID
    private static String pushId;
    private static String token;
    private static  String userId;
    private static String account;

    /**
     * 存储数据到SP--XML中，持久化
     */
    private static void save(Context context) {
        // TODO: 2020/1/11 待抽离工具类
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);

        sp.edit()
                .putString(KEY_PUSH_ID, pushId)
                .putBoolean(KEY_IS_BIND, isBind)
                .putString(KEY_TOKEN,token)
                .putString(KEY_ACCOUNT,account)
                .putString(KEY_USER_ID,userId)
                .apply();
    }

    /**
     * 进行数据加载
     *
     * @param context
     */
    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID, "");
        isBind=sp.getBoolean(KEY_IS_BIND,false);
        token=sp.getString(KEY_TOKEN,"");
        userId=sp.getString(KEY_USER_ID,"");
        account=sp.getString(KEY_ACCOUNT,"");
    }

    /**
     * 设置并存储设备的ID--sp
     *
     * @param pushId
     */
    public static void setPushId(String pushId) {
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    public static String getPushId() {
        return pushId;
    }

    /**
     * 返回当前账户的状态----可以设置一个默认登陆，省的每次都查...
     *
     * @return
     */
    public static boolean isLogined() {
        //用户ID和Token不为空
        return !TextUtils.isEmpty(userId)
                &&!TextUtils.isEmpty(token);
    }

    /**
     * 是否已经完善了用户信息
     * @return
     */
    // TODO: 2020/1/11 逻辑需要完善
    public static boolean isCompleted(){
        if (isLogined()){
            User self=getUser();
            return !TextUtils.isEmpty(self.getDesc())
                    &&!TextUtils.isEmpty(self.getPortrait())
                    &&self.getSex()!=0;
        }

        return  false;
    }
    
    /**
     * 是否已经绑定设备了，也就是绑定数据已经上传到服务器了
     *
     * @return
     */
    public static boolean isBind() {
        return isBind;
    }

    /**
     * 设置绑定状态--sp
     * @param isBind
     */
    public static void setBind(boolean isBind){
       Account.isBind=isBind;
       Account.save(Factory.app());
    }

    /**
     * 将个人信息保存到SP中
     * @param
     */
    public static  void login(AccountRspModel model){
      Account.token=model.getToken();
      Account.account=model.getAccount();
      Account.userId=model.getUser().getId();
      save(Factory.app());
    }

    /**
     * 从数据库中查询当前用户信息
     * @return
     */
    public static User getUser(){
        //userId若为null，new一个
        return TextUtils.isEmpty(userId)?
                new User(): SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    /**
     * 返回用户Id
     * @return
     */
    public static String getUserId(){
      return   getUser().getId();
    }

    /**
     * 获取当前用户的Token
     * @return
     */
    public static String getToken(){
        return token;
    }
}
