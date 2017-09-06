package com.li.rxjava2.httpRequest;

import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.li.rxjava2.api.ApiManager;
import com.li.rxjava2.bean.Constant;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Lee on 2017/9/6 0006.
 */

public class RetrofitService {

    private static class INSATNCE{
        public final static RetrofitService instance = new RetrofitService();
    }

    public static RetrofitService getInstance(){
        return INSATNCE.instance;
    }

    public Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //支持rxjava
                .build();
        return retrofit;
    }
}
