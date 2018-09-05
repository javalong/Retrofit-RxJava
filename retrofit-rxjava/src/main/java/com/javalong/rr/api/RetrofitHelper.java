package com.javalong.rr.api;


import android.content.Context;

import com.javalong.rr.lib.TWInterceptor;

import java.io.File;
import java.lang.reflect.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by javalong on 2016/5/29.
 * retrofit2
 */
public class RetrofitHelper {
    public static String BASE_URL;
    private static RetrofitHelper instance;

    private Map<Class, Object> apiMap;
    private OkHttpClient mOkHttpClient;
    private Context mContext;
    private Retrofit mRetrofit;
    private TWInterceptor mInterceptor;
    private boolean needMock = false;

    public static RetrofitHelper getInstance() {
        if (instance == null) {
            synchronized (RetrofitHelper.class) {
                if (instance == null) {
                    instance = new RetrofitHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 启动后初始化
     */
    public void init(Context context, String baseUrl, boolean needMock) {
        BASE_URL = baseUrl;
        mContext = context;
        initDefaultOkHttpClient();
        apiMap = new HashMap<>();
        this.needMock = needMock;
        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).
                addCallAdapterFactory(CustomRxJava2CallAdapterFactory.create()).
                client(mOkHttpClient).
                build();
    }


    /**
     * 自定义OkHttpClient
     */
    public void init(Context context, String baseUrl, OkHttpClient client, boolean needMock) {
        mOkHttpClient = client;
        init(context, baseUrl, needMock);
    }


    /**
     * 自定义Interceptor
     */
    public void init(Context context, String baseUrl, TWInterceptor interceptor, boolean needMock) {
        mInterceptor = interceptor;
        init(context, baseUrl, needMock);
    }

    /**
     * 注册Api接口
     */
    public <T> void registerApi(Class<T> cls) {
        if (mContext == null) {
            throw new RuntimeException("need to run init method!");
        }
        if (cls != null && !apiMap.containsKey(cls)) {
            T api = mRetrofit.create(cls);
            if (needMock) {
                apiMap.put(cls, Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{cls}, new MockHandler(mRetrofit, mContext, api)));
            } else {
                apiMap.put(cls, api);
            }
        }
    }

    public <T> T getApi(Class<T> cls) {
        if (mContext == null) {
            throw new RuntimeException("need to run init method!");
        }
        if (!apiMap.containsKey(cls)) {
            throw new RuntimeException("need to run registerApi!");
        }
        return (T) apiMap.get(cls);
    }

    /**
     * 初始化okHttp
     */
    private void initDefaultOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    try {
                        X509TrustManager xtm = new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] x509Certificates = new X509Certificate[0];
                                return x509Certificates;
                            }
                        };
                        SSLContext sslContext = null;
                        try {
                            sslContext = SSLContext.getInstance("SSL");

                            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (KeyManagementException e) {
                            e.printStackTrace();
                        }
                        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        };

                        // 指定缓存路径,缓存大小100Mb
                        Cache cache = new Cache(new File(mContext.getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
                        if (mInterceptor == null) {
                            mInterceptor = new TWInterceptor();
                        }
                        mOkHttpClient = new OkHttpClient.Builder().
                                addInterceptor(mInterceptor).
                                retryOnConnectionFailure(false).
                                connectTimeout(30, TimeUnit.SECONDS).
                                sslSocketFactory(sslContext.getSocketFactory()).
                                hostnameVerifier(DO_NOT_VERIFY).
                                cache(cache).
                                build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}