package com.li.rxjava2.api;

import com.li.rxjava2.bean.WeatherData;


import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lee on 2017/9/6 0006.
 *
 * 获取天气的接口
 */

public interface ApiManager {


    /**
     * 只拼接"q"参数
     * @param cityName
     * @return
     */
    @GET("weather?appid=34e66d9a265a292ed06a2391f2d8ff8a")
    Observable<WeatherData> getWeatherByName(@Query("q") String cityName);

    /**
     * 根据城市名字获取
     * 完整的接口例子  http://api.openweathermap.org/data/2.5/weather?q=Chengdu&appid=34e66d9a265a292ed06a2391f2d8ff8a
     * @param cityName
     * @param appid
     * @return
     */
    @GET("weather")
        Observable<WeatherData> getWeatherByName( @Query("q") String cityName,@Query("appid")String appid);

    /**
     * 根据城市ID获取
     * 例子 http://api.openweathermap.org/data/2.5/weather?id=2172797&appid=34e66d9a265a292ed06a2391f2d8ff8a
     * @param cityID
     * @param appid
     * @return
     */
    @GET("weather")
    Observable<WeatherData> getWeatherByID(@Query("id") String cityID,@Query("appid") String appid);

    /**
     * 根据地理位置（经纬度）获取
     * 例子 http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=34e66d9a265a292ed06a2391f2d8ff8a
     * @param latitude
     * @param lontitude
     * @param appid
     * @return
     */
    @GET("weather")
    Observable<WeatherData> getWeatherByGeoCoor(@Query("lat") String latitude,@Query("lon") String lontitude,@Query("appid") String appid);

}
