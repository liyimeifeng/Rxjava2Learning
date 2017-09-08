package com.li.rxjava2.httpRequest;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.li.rxjava2.bean.Constant;

import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Lee on 2017/9/6 0006.
 *
 * Retrofit 包装类
 */

public class RetrofitWrapper {

    private Retrofit retrofit;

    private static class INSATNCE{
        public final static RetrofitWrapper instance = new RetrofitWrapper();
    }

    public static RetrofitWrapper getInstance(){
        return INSATNCE.instance;
    }


    public Retrofit getRetrofit() {
         retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BaseUrl)
                 .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //支持rxjava1
                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //支持rxjava2
                .build();


        return retrofit;
    }

    /**
     * 创建请求接口对象
     * @param clas
     * @param <T> 泛型
     * @return
     */
    public <T> T creat(Class<T> clas){
        return retrofit.create(clas);
    }


}
