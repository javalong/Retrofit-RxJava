package com.javalong.retrofit_rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.javalong.retrofit_rxjava.api.ServerApi;
import com.javalong.retrofit_rxjava.utils.FileUtils;
import com.javalong.rr.api.RetrofitHelper;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

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
            if (TextUtils.isEmpty(etUrl.getText().toString())) {
                Toast.makeText(this, "请输入url", Toast.LENGTH_SHORT).show();
                return;
            }
            RetrofitHelper.getInstance().getApi(ServerApi.class)
                    .download(etUrl.getText().toString())
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) {
                            FileUtils.saveFileAndInstall(DownloadActivity.this, responseBody);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

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
