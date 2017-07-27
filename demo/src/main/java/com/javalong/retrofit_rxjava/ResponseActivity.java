package com.javalong.retrofit_rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.javalong.rr.api.RetrofitHelper;
import com.javalong.retrofit_rxjava.api.ServerApi;
import com.javalong.retrofit_rxjava.bean.TestBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;

/**
 * Created by 令狐 on 17/7/26.
 */

public class ResponseActivity extends AppCompatActivity {
    protected TextView tvResponseString;
    protected TextView tvConvert;
    protected TextView tvRequestUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_response);
        initView();
        int position = getIntent().getIntExtra("position", 0);
        switch (position){
            case 0:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"getSuccess");
                RetrofitHelper.getInstance().getApi(ServerApi.class).get("getSuccess",true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).getSuccess()
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
                break;
            case 1:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"postSuccess");
                RetrofitHelper.getInstance().getApi(ServerApi.class).post("postSuccess",true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).postSuccess()
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
                break;
            case 2:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"postStringSuccess");
                RetrofitHelper.getInstance().getApi(ServerApi.class).post("postStringSuccess",true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).postStringSuccess()
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvConvert.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvConvert.setText(s.toString());
                            }
                        });
                break;
            case 3:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"postIntSuccess");
                RetrofitHelper.getInstance().getApi(ServerApi.class).post("postIntSuccess",true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).postIntSuccess()
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvConvert.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(Integer i) {
                                tvConvert.setText(""+i);
                            }
                        });
                break;
            case 4:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"postBoolSuccess");
                RetrofitHelper.getInstance().getApi(ServerApi.class).post("postBoolSuccess",true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).postBoolSuccess()
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvConvert.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(Boolean i) {
                                tvConvert.setText(""+i);
                            }
                        });
                break;
            case 5:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"postListSuccess");
                RetrofitHelper.getInstance().getApi(ServerApi.class).post("postListSuccess",true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).postListSuccess()
                        .subscribe(new Observer<List<TestBean>>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvConvert.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(List<TestBean> list) {
                                tvConvert.setText(""+list);
                            }
                        });
                break;
            case 6:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"postSuccessWithParam");
                Map<String,String > paramMap = new HashMap<>();
                paramMap.put("param","test");
                RetrofitHelper.getInstance().getApi(ServerApi.class).post("postSuccessWithParam",paramMap)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).postSuccessWithParam("test")
                        .subscribe(new Observer<TestBean>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvConvert.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(TestBean testBeen) {
                                tvConvert.setText(""+testBeen);
                            }
                        });
                break;
            case 7:
                tvRequestUrl.setText(RetrofitHelper.BASE_URL+"postFailed");
                RetrofitHelper.getInstance().getApi(ServerApi.class).post("postFailed",true)
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvResponseString.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(String s) {
                                tvResponseString.setText(s);
                            }
                        });
                RetrofitHelper.getInstance().getApi(ServerApi.class).postFailed()
                        .subscribe(new Observer<TestBean>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                                tvConvert.setText(e.getMessage());
                            }
                            @Override
                            public void onNext(TestBean testBeen) {
                                tvConvert.setText(""+testBeen);
                            }
                        });
                break;
        }
    }

    private void initView() {
        tvResponseString = (TextView) findViewById(R.id.tvResponseString);
        tvConvert = (TextView) findViewById(R.id.tvConvert);
        tvRequestUrl = (TextView) findViewById(R.id.tvRequestUrl);
    }
}
