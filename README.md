# Retrofit-RxJava
***
#### 项目简介

采用Retrofit+RxJava作为Http框架

blog:http://www.jianshu.com/p/17e3e3102c1f

***
#### 新版本特性
* 添加单图／多图上传demo
* 添加上传图片进度接口
* demo gif
![upload image](https://github.com/javalong/Retrofit-RxJava/blob/master/images/upload_image_demo.gif)

***
#### 快速接入

1. 添加项目 module 或引入依赖
   ```java
       compile 'com.javalong:retrofit-rxjava:1.0.0'
   ```

2. 在使用之前调用代码初始化
   ```java
       RetrofitHelper.getInstance().init(this,baseUrl);
   ```
    baseUrl作为http请求的前缀.

3. 注册自己需要的Api

   ```java
       RetrofitHelper.getInstance().registerApi(ServerApi.class);
   ```

4. 添加自己的Http请求

    模仿demo中的`ServerApi.java`中的Http请求，自定义自己的Http请求

    也可参考上面的blog.

5. 调用方法Http请求

    参考demo

***

#### 功能介绍

1. 主要支持如下json格式

    ```json
    {
       data:{},
       errorCode:200,
       moreInfo:"",
       ...
       ...
    }
    ```
    可以同级添加需要的字段，主要的数据在data中。

    可以在`ResponseMessageBean.java`自定义字段

2. 支持转化基本类型

    ```java
     Observable<Boolean> postBoolSuccess();
     Observable<Integer> postIntSuccess();
     ...
     ...
    ```

3. 支持不处理操作，直接返回请求的内容

    ```java
    @GET("{requestUrl}")
    Observable<String> get(@Path("requestUrl") String requestUrl, @Header("origion")boolean origion);
    ```
    在任意的请求方法中多添加一个参数`@Header("origion")boolean origion`,然后传入`true`

    具体实现：

    `TWInterceptor.java`

    ```java

       ...
       if (origion != null && origion) {
            return onSuccess(originalResponse, jsonString);
        }
       ...
        String origion = newRequest.header("origion");
       ...
    ```

4. 支持泛型转换

    ```java
     //ServerApi.java中声明
     @POST("postSuccess")
     Observable<TestBean> postSuccess();

     //调用后直接返回解析好的内容
     RetrofitHelper.getApi().postSuccess()
                             .subscribe(new Observer<TestBean>() {
                                 @Override
                                 public void onCompleted() {
                                 }
                                 @Override
                                 public void onError(Throwable e) {
                                     tvConvert.setText(e.getMessage());
                                 }
                                 @Override
                                 public void onNext(TestBean s) {
                                     tvConvert.setText(s.toString());
                                 }
                             });
    ```

    转换成泛型对象，具体使用到的数据，是返回的json对象中的data字段。

5. 支持List泛型转换

    ```java
     @POST("postListSuccess")
     Observable<List<TestBean>> postListSuccess();
    ```

6. 支持请求失败处理

    1. 在本项目中如果errorCode!=200认为是失败，会抛出异常，进入RxJava的onError回调。

    2. 如果是网络问题，造成的请求失败，同样也会进入onError回调。

7. 支持https

8. 支持自定义OkHttpClient

    初始化时,可以调用

    ```java
       RetrofitHelper.getInstance().init(this,baseUrl,okHttpClient);
    ```

    默认OkHttpClient为

    ```java
        Cache cache = new Cache(new File(mContext.getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
        mOkHttpClient = new OkHttpClient.Builder().
                addInterceptor(mInterceptor).
                retryOnConnectionFailure(false).
                connectTimeout(30, TimeUnit.SECONDS).
                sslSocketFactory(sslContext.getSocketFactory()).
                hostnameVerifier(DO_NOT_VERIFY).
                cache(cache).
                build();
    ```
    100M缓存，30m秒超时，失败不重试

9. 支持自定义Interceptor

    interceptor是okHttpClient的一个属性，如果不需要全部自定义OkHttpClicent，只想自定义Interceptor
    可以调用.
    ```java
        RetrofitHelper.getInstance().init(this,baseUrl,interceptor);
    ```
    传入的interceptor需要继承lib中的TWInterceptor.

    作用：
    1. 自定义 onSuccess回调。
    2. 支持用户对某一类errorCode做统一处理在 onFailed中
    3. 自定义UserAgent

10. 优化链式结构

    不需要在请求中都添加
    ```java
        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    ```
    已经在`TWJavaCallAdapterFactory.java`中全局添加

***
#### [更新日志](https://github.com/javalong/Retrofit-RxJava/blob/master/UPDATE.md)
***
#### 感谢（Thanks）
**[CircleProgress](https://github.com/lzyzsd/CircleProgress)** 进度条
**[RxGalleryFinal](https://github.com/FinalTeam/RxGalleryFinal)** 相册