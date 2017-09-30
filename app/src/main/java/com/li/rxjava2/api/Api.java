package com.li.rxjava2.api;

import com.li.rxjava2.httpRequest.UserBaseInfoRequest;
import com.li.rxjava2.httpRequest.UserBaseInfoResponse;
import com.li.rxjava2.httpRequest.UserExtraInfoRequest;
import com.li.rxjava2.httpRequest.UserExtraInfoResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Created by Lee on 2017/9/30 0030.
 */

public interface Api {

    /**
     * 基本信息
     * @param request
     * @return
     */
    @GET
    Observable<UserBaseInfoResponse> getUserBaseInfo(@Body UserBaseInfoRequest request);


    /**
     * 额外信息
     * @param request
     * @return
     */
    @GET
    Observable<UserExtraInfoResponse> getUserExtraInfo(@Body UserExtraInfoRequest request);

}
