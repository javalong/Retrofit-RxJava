package com.javalong.rr.api;

import android.content.Context;
import android.util.Log;

import com.javalong.rr.annotation.MOCK;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class MockHandler<T> implements InvocationHandler {

    T api;
    Context mContext;
    Retrofit retrofit;

    public MockHandler(Retrofit retrofit, Context context, T api) {
        this.api = api;
        mContext = context;
        this.retrofit = retrofit;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean isExist = method.isAnnotationPresent(MOCK.class);
        if (isExist) {
            MOCK mock = method.getAnnotation(MOCK.class);
            if (!mock.enable()) {
                return method.invoke(api, args);
            } else {
                if (mock.value().startsWith("http")) {
                    //如果是http的 就尝试自己去请求,就自己修改下url 然后请求
                    preLoadServiceMethod(method, mock.value());
                    return method.invoke(api, args);
                } else {
                    //认为是在assets中
                    String response = readAssets(mock.value());
                    Object obj = retrofit.nextCallAdapter(null, method.getGenericReturnType(), method.getAnnotations()).adapt(new MockCall(response));
                    return obj;
                }
            }
        } else {
            //如果method有mock注解，就处理下，如果没有，就直接调用后返回
            return method.invoke(api, args);
        }
    }

    private void preLoadServiceMethod(Method method, String relativeUrl) {
        try {
            Method m = Retrofit.class.getDeclaredMethod("loadServiceMethod", Method.class);
            m.setAccessible(true);
            Object serviceMethod = m.invoke(retrofit, method);
            Field field = serviceMethod.getClass().getDeclaredField("relativeUrl");
            field.setAccessible(true);
            field.set(serviceMethod, relativeUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifyAnnotationValue(Annotation annotation, String value) {
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
            // 因为这个字段事 private final 修饰，所以要打开权限
            declaredField.setAccessible(true);
            // 获取 memberValues
            Map memberValues = null;
            memberValues = (Map) declaredField.get(invocationHandler);
            // 修改 value 属性值
            memberValues.put("value", value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String readAssets(String fileName) {
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }

}


class MockCall<T> implements Call<T> {

    String data;

    public MockCall(String data) {
        this.data = data;
    }

    private Response getResponse() {
        return Response.success(data);
    }

    @Override
    public Response<T> execute() throws IOException {
        return getResponse();
    }

    @Override
    public void enqueue(Callback<T> callback) {
        callback.onResponse(null, getResponse());
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public Call<T> clone() {
        return this;
    }

    @Override
    public Request request() {
        return null;
    }
}
