package com.whf.messagerelayer.api;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/12/13
 * @discription null
 * @usage null
 */
public interface Api {
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("/")
    Observable<ResponseBody> getResult(@Body RequestBody requestBody);
}