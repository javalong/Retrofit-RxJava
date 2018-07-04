package com.javalong.retrofit_rxjava;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.javalong.rr.api.RetrofitHelper;
import com.javalong.retrofit_rxjava.api.ServerApi;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashHandler.getInstance().init(this);
        setContentView(R.layout.activity_main);
        RetrofitHelper.getInstance().init(this, "http://rap.kongge.com/mockjsdata/37/", true);
        RetrofitHelper.getInstance().registerApi(ServerApi.class);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                Button btRequest = (Button) holder.itemView.findViewById(R.id.btRequest);
                switch (position) {
                    case 0:
                        btRequest.setText("GetSuccess");
                        break;
                    case 1:
                        btRequest.setText("PostSuccess");
                        break;
                    case 2:
                        btRequest.setText("postStringSuccess");
                        break;
                    case 3:
                        btRequest.setText("postIntSuccess");
                        break;
                    case 4:
                        btRequest.setText("postBoolSuccess");
                        break;
                    case 5:
                        btRequest.setText("postListSuccess");
                        break;
                    case 6:
                        btRequest.setText("postSuccessWithParam");
                        break;
                    case 7:
                        btRequest.setText("postFailed");
                        break;
                    case 8:
                        btRequest.setText("postImage");
                        break;
                    case 9:
                        btRequest.setText("downloadFile");
                        break;
                    case 10:
                        btRequest.setText("mockdata");
                        break;
                }
                btRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestDataByIndex(position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return 11;
            }
        });
    }


    private void requestDataByIndex(int position) {
        if (position < 8) {
            Intent intent = new Intent();
            intent.setClass(this, ResponseActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        } else if (position == 8) {
            Intent intent = new Intent();
            intent.setClass(this, ImageUploadActivity.class);
            startActivity(intent);
        } else if (position == 9) {
            Intent intent = new Intent();
            intent.setClass(this, DownloadActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(this, MockDataActivity.class);
            startActivity(intent);
        }
    }
}
