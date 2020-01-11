package com.rye.factory.net;

import com.rye.catcher.common.Common;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * CreateBy ShuQin
 * at 2020/1/5
 */
public class NetWork {
    public static Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Retrofit.Builder builder = new Retrofit.Builder();

        return builder.baseUrl(Common.Constance.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
