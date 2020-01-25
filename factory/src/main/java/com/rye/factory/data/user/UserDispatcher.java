package com.rye.factory.data.user;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.rye.factory.data.helper.DbHelper;
import com.rye.factory.model.card.UserCard;
import com.rye.factory.model.db.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CreateBy ShuQin
 * at 2020/1/24
 */
public class UserDispatcher implements UserCenter {
    private static UserDispatcher instance;
    /**
     * 单线程线程池
     */
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static UserDispatcher instance(){
        if (instance==null){
            synchronized (UserDispatcher.class){
                if (instance==null){
                    instance=new UserDispatcher();
                }
            }
        }
        return instance;
    }
    @Override
    public void dispatch(UserCard... userCards) {
           executor.execute(new UserCardHandler(userCards));
    }

    private class  UserCardHandler implements  Runnable{
        private final  UserCard[] cards;

        private UserCardHandler(UserCard[] userCards) {
            cards = userCards;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            List<User> users=new ArrayList<>();
            Arrays.stream(cards).forEach(card->{
                if (card!=null&&!TextUtils.isEmpty(card.getId())){
                    users.add(card.build());
                }
            });
            //进行数据库存储，并分发通知，异步的操作
            DbHelper.save(User.class,users.toArray(new User[0]));
        }
    }

}
