package com.li.rxjava2.api;

import com.li.rxjava2.bean.WeatherData;


import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lee on 2017/9/6 0006.
 */

public interface ApiManager {

        @GET("weather")
        Observable<WeatherData> getWeather( @Query("q") String cityName,@Query("appid")String appid);

}
