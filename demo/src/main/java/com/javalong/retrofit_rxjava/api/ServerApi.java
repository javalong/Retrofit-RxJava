package com.javalong.retrofit_rxjava.api;


import com.javalong.retrofit_rxjava.bean.TestBean;

import java.util.List;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by javalong on 16-11-11.
 */

public interface ServerApi {


    /**
     * ==================通用的=======================
     * @param origion 是否不做任何处理，true 就直接返回原始字符串
     */
    @GET("{requestUrl}")
    Observable<String> get(@Path("requestUrl") String requestUrl, @Header("origion")boolean origion);

    @GET("{requestUrl}")
    Observable<String> get(@Path("requestUrl") String requestUrl);

    @POST("{requestUrl}")
    Observable<String> post(@Path("requestUrl") String requestUrl, @Header("origion")boolean origion);

    @POST("{requestUrl}")
    Observable<String> post(@Path("requestUrl") String requestUrl);

    @POST("{requestUrl}")
    @FormUrlEncoded
    Observable<String> post(@Path("requestUrl") String requestUrl, @FieldMap Map<String, String> paramMap);


    /**
     * ==================特定的=========================
     */
    @GET("getSuccess")
    Observable<TestBean> getSuccess();

    @POST("postSuccess")
    Observable<TestBean> postSuccess();

    @POST("postStringSuccess")
    Observable<String> postStringSuccess();

    @POST("postIntSuccess")
    Observable<Integer> postIntSuccess();

    @POST("postBoolSuccess")
    Observable<Boolean> postBoolSuccess();

    @POST("postListSuccess")
    Observable<List<TestBean>> postListSuccess();

    /**
     * post 添加参数 必须添加 @FormUrlEncoded
     */
    @POST("postSuccessWithParam")
    @FormUrlEncoded
    Observable<TestBean> postSuccessWithParam(@Field("param") String param);

    @POST("postFailed")
    Observable<TestBean> postFailed();


}
