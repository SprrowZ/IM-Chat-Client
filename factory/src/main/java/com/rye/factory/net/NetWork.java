package com.rye.factory.net;

import android.text.TextUtils;

import com.rye.catcher.common.Common;
import com.rye.factory.persistence.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public class NetWork {


    private static NetWork instance;
    private Retrofit retrofit;

    // TODO: 2020/1/11 查看静态代码块的时机
    static {
        instance=new NetWork();
    }


    public static Retrofit getRetrofit() {

        if (instance.retrofit!=null){
            return  instance.retrofit;
        }
        /**
         * 请求头里的token一定不要少了
         */
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original=chain.request();
                        Request.Builder builder=original.newBuilder();
                        if (!TextUtils.isEmpty(Account.getToken())){
                            builder.addHeader("token",Account.getToken());
                        }
                        builder.addHeader("Content-Type","application/json");
                        Request newRequest=builder.build();

                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit.Builder builder = new Retrofit.Builder();

        instance.retrofit= builder.baseUrl(Common.Constance.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return instance.retrofit;
    }

    /**
     * 返回一个请求代理
     * @return
     */
    public static RemoteService remote(){
        return NetWork.getRetrofit().create(RemoteService.class);
    }
}
