package com.javalong.retrofit_rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.javalong.retrofit_rxjava.api.ServerApi;
import com.javalong.retrofit_rxjava.utils.FileUtils;
import com.javalong.rr.api.RetrofitHelper;

import okhttp3.ResponseBody;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by 令狐 on 17/10/26.
 */

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {
    protected EditText etUrl;
    protected Button btDown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_download);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_down) {
            if(TextUtils.isEmpty(etUrl.getText().toString())){
                Toast.makeText(this, "请输入url", Toast.LENGTH_SHORT).show();
                return;
            }
            RetrofitHelper.getInstance().getApi(ServerApi.class)
                    .download(etUrl.getText().toString())
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<ResponseBody>() {
                        @Override
                        public void call(ResponseBody responseBody) {
                            FileUtils.saveFileAndInstall(DownloadActivity.this, responseBody);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    });
        }
    }

    private void initView() {
        etUrl = (EditText) findViewById(R.id.et_url);
        btDown = (Button) findViewById(R.id.bt_down);
        btDown.setOnClickListener(DownloadActivity.this);
    }
}
