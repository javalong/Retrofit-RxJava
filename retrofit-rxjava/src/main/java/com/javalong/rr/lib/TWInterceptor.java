package com.javalong.rr.lib;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by 令狐 on 17/7/27.
 * 具体的请求处理
 */

public class TWInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        /**
         * 统一设置请求头
         */
        Request newRequest = dealRequestData(createRequestHeader(request.newBuilder()).build());
        Response originalResponse = chain.proceed(newRequest);
        //如果是重定向，那么就执行重定向后返回数据。
        if (originalResponse.isRedirect()) {
            Request redirectRequest = request.newBuilder().url(originalResponse.header("location")).build();
            originalResponse = chain.proceed(redirectRequest);
        }
        String origion = newRequest.header("origion");
        originalResponse = dealResponseData(Boolean.parseBoolean(origion), originalResponse);
        return originalResponse;
    }

    protected Request dealRequestData(Request newRequest) {
        return newRequest;
    }

    /**
     * 统一处理请求头部数据
     *
     * @return
     */

    protected Request.Builder createRequestHeader(Request.Builder builder) {
        builder.header("Content-Type",
                "application/x-www-form-urlencoded");
        builder.header("User-Agent", getUserAgent());
        return builder;
    }


    /**
     * 统一处理原始数据
     *
     * @param origion          是否需要原生的 不转化的数据
     * @param originalResponse
     */
    protected Response dealResponseData(Boolean origion, Response originalResponse) {
        String jsonString = null;
        try {
            BufferedSource bufferedSource = originalResponse.body().source();
            MediaType contentType = originalResponse.body().contentType();
            if ("octet-stream".equals(contentType.subtype())) {
                //请求文件，就直接返回ResponseBody;
                return originalResponse.newBuilder().
                        body(ResponseBody.create(contentType, originalResponse.body().contentLength(), bufferedSource)).
                        build();
            } else {
                jsonString = bufferedSource.readString(Charset.forName("utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (origion != null && origion) {
            return onSuccess(originalResponse, jsonString);
        }
        ResponseMessageBean msgBean = ResponseMessageBean.analyseReponse(jsonString);
        if (msgBean == null) return onSuccess(originalResponse, msgBean.data.toString());
        if (msgBean != null && (msgBean.errorCode == 200)) {
            if (msgBean.data != null) {
                return onSuccess(originalResponse, msgBean.data.toString());
            } else {
                return originalResponse.newBuilder().body(null).build();
            }
        } else {
            onFailed(msgBean);
            throw new HttpError(msgBean, msgBean.error);
        }
    }


    protected static Response onSuccess(Response originalResponse, String content) {
        return originalResponse.newBuilder().
                body(ResponseBody.create(null, content)).
                build();
    }


    /**
     * errorCode 不为200
     *
     * @param msgBean
     */
    protected void onFailed(ResponseMessageBean msgBean) {
        String alert = "";
        if (msgBean == null) {
            return;
        }
        if (msgBean.errorCode != 200) {
            //TODO 自定义错误处理
        }
    }

    //TODO 自定义userAgent
    protected String getUserAgent() {
        return "";
    }

}