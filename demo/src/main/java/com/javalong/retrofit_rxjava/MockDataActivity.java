package com.javalong.retrofit_rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.javalong.retrofit_rxjava.api.ServerApi;
import com.javalong.retrofit_rxjava.bean.MockBean;
import com.javalong.rr.api.RetrofitHelper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MockDataActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MockData";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mockdata);

        findViewById(R.id.btMock1).setOnClickListener(this);
        findViewById(R.id.btMock2).setOnClickListener(this);
        findViewById(R.id.btMock3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btMock1:
                RetrofitHelper.getInstance().getApi(ServerApi.class)
                        .mock1()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String s) {
                                Log.e(TAG, "返回：" + s);
                            }
                        });
                break;
            case R.id.btMock2:
                RetrofitHelper.getInstance().getApi(ServerApi.class)
                        .mock2()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MockBean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }
                            @Override
                            public void onNext(MockBean s) {
                                Log.e(TAG, "返回：" + s.toString());
                            }

                            @Override
                            public void onError(Throwable t) {
                                Log.e(TAG, "返回：" + t.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
            case R.id.btMock3:
                RetrofitHelper.getInstance().getApi(ServerApi.class)
                        .mock3()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onComplete() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "返回：" + e.getMessage());
                            }

                            @Override
                            public void onNext(String s) {
                                Log.e(TAG, "返回：" + s);
                            }
                        });
                break;
        }
    }
}
