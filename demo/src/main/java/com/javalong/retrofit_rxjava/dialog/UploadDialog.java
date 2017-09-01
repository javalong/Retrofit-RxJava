package com.javalong.retrofit_rxjava.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.javalong.retrofit_rxjava.ImageUploadActivity;
import com.javalong.retrofit_rxjava.R;
import com.javalong.retrofit_rxjava.bean.ImageUploadBean;
import com.javalong.retrofit_rxjava.divider.ItemDecorationAlbumColumns;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 令狐 on 17/8/31.
 */

public class UploadDialog extends AlertDialog {
    public final static int INTERVAL_MILLITIME = 50;
    protected View rootView;
    protected RecyclerView recyclerView;
    private List<ImageUploadBean> dataList;
    private Handler handler;
    private int updateCount = 0;

    public UploadDialog(@NonNull Context context) {
        super(context, R.style.UploadDialogStyle);
        this.dataList = new ArrayList<>();
        handler = new Handler();
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_upload, null);
        setView(rootView);
        setCancelable(false);
        initView(rootView);
        setTitle("上传图片");
        setButton(BUTTON_POSITIVE, "确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(20,2));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                ImageUploadBean imageUploadBean = dataList.get(position);
                ImageView ivUpload = (ImageView) holder.itemView.findViewById(R.id.iv_upload);
                DonutProgress donutProgress = (DonutProgress) holder.itemView.findViewById(R.id.donut_progress);
                File file = new File(imageUploadBean.getOriginPath());
                Picasso.with(getContext()).load(Uri.fromFile(file)).into(ivUpload);
                if (imageUploadBean.getProgress() == 100&&donutProgress.getAlpha()==1) {
                    ViewCompat.animate(donutProgress).alpha(0).setDuration(500).setStartDelay(1000).start();
                } else {
                    donutProgress.setAlpha(1);
                }
                donutProgress.setProgress(imageUploadBean.getProgress());
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }
        });
    }

    private boolean checkFinished() {
        if(dataList==null||dataList.size()==0)return true;
        for(ImageUploadBean uploadBean:dataList){
            if(uploadBean.getProgress()<100)return false;
        }
        return true;
    }


    public void updateProgress(final List<ImageUploadBean> imageList) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataList.clear();
                dataList.addAll(imageList);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }, updateCount * INTERVAL_MILLITIME);
        updateCount++;
    }

    private void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
    }

    @Override
    public void show() {
        super.show();
        getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFinished()) {
                    updateCount = 0;
                    dismiss();
                }else{
                    Toast.makeText(getContext(), "请等待上传完毕", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
