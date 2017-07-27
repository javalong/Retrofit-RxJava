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

import com.javalong.retrofit.api.RetrofitHelper;
import com.javalong.retrofit_rxjava.api.ServerApi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RetrofitHelper.getInstance().init(this,"http://rap.kongge.com/mockjsdata/37/");
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
                return 8;
            }
        });
    }



    private void requestDataByIndex(int position) {
        Intent intent = new Intent();
        intent.setClass(this,ResponseActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
