package com.javalong.retrofit_rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.javalong.retrofit_rxjava.api.ServerApi;
import com.javalong.retrofit_rxjava.bean.ImageUploadBean;
import com.javalong.retrofit_rxjava.dialog.UploadDialog;
import com.javalong.rr.api.RetrofitHelper;
import com.javalong.rr.lib.IProgressListener;
import com.javalong.rr.lib.RequestBodyWithProgress;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by 令狐 on 17/8/31.
 */

public class ImageUploadActivity extends AppCompatActivity {
    public final static String TAG = "ImageUploadActivity";
    private UploadDialog uploadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageupload);
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
                        btRequest.setText("UPLOAD SINGLE IMAGE");
                        break;
                    case 1:
                        btRequest.setText("UPLOAD MULTI IMAGE");
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
                return 2;
            }
        });
    }

    private void requestDataByIndex(int position) {
        switch (position) {
            case 0:
                uploadSingleImage();
                break;
            case 1:
                uploadMultiImage();
                break;
        }
    }

    /**
     * 上传多张图片
     */
    private void uploadMultiImage() {
        uploadDialog = new UploadDialog(this);
        RxGalleryFinal
                .with(this)
                .image()
                .multiple()
                .maxSize(4)
                .imageLoader(ImageLoaderType.PICASSO)
                .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(final ImageMultipleResultEvent imageRadioResultEvent) throws Exception {
                        uploadDialog.show();
                        Map<String, RequestBody> partMap = new HashMap<String, RequestBody>();
                        final List<ImageUploadBean> imageUploadBeanList = new ArrayList<ImageUploadBean>();
                        for (int i = 0; i < imageRadioResultEvent.getResult().size(); i++) {
                            MediaBean mediaBean = imageRadioResultEvent.getResult().get(i);
                            imageUploadBeanList.add(new ImageUploadBean(mediaBean.getOriginalPath(), 0));
                        }
                        uploadDialog.updateProgress(imageUploadBeanList);
                        for (int i = 0; i < imageRadioResultEvent.getResult().size(); i++) {
                            final int index = i;
                            final MediaBean mediaBean = imageRadioResultEvent.getResult().get(i);
                            File file = new File(mediaBean.getOriginalPath());
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            RequestBodyWithProgress requestBodyWithProgress = new RequestBodyWithProgress(requestBody, new IProgressListener() {
                                @Override
                                public void onProgressUpdated(long currentLength, long maxLength) {
                                    ImageUploadBean uploadBean = imageUploadBeanList.get(index);
                                    uploadBean.setOriginPath(mediaBean.getOriginalPath());
                                    uploadBean.setProgress((int) (currentLength * 100 / maxLength));
                                    List<ImageUploadBean> newList = new ArrayList<ImageUploadBean>();
                                    for (ImageUploadBean bean : imageUploadBeanList) {
                                        newList.add(new ImageUploadBean(bean.getOriginPath(), bean.getProgress()));
                                    }
                                    uploadDialog.updateProgress(newList);
                                }
                            });

                            partMap.put("key" + i, requestBodyWithProgress);
                        }

                        RetrofitHelper.getInstance().getApi(ServerApi.class).postMultiImage(partMap)
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(ImageUploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                    }
                                });
                    }
                })
                .openGallery();
    }

    /**
     * 上传单张图片
     */
    private void uploadSingleImage() {
        if (uploadDialog == null) {
            uploadDialog = new UploadDialog(this);
        }
        RxGalleryFinal
                .with(this)
                .image()
                .crop(false)
                .radio()
                .imageLoader(ImageLoaderType.PICASSO)
                .subscribe(new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(final ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        uploadDialog.show();
                        File file = new File(imageRadioResultEvent.getResult().getOriginalPath());
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        RequestBodyWithProgress requestBodyWithProgress = new RequestBodyWithProgress(requestBody, new IProgressListener() {
                            @Override
                            public void onProgressUpdated(long currentLength, long maxLength) {
                                List<ImageUploadBean> imageUploadBeanList = new ArrayList<ImageUploadBean>();
                                imageUploadBeanList.add(new ImageUploadBean(imageRadioResultEvent.getResult().getOriginalPath(), (int) (currentLength * 100 / maxLength)));
                                uploadDialog.updateProgress(imageUploadBeanList);
                            }
                        });
                        MultipartBody.Part part = MultipartBody.Part.create(requestBodyWithProgress);
                        RetrofitHelper.getInstance().getApi(ServerApi.class).postSingleImage(part)
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(ImageUploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                    }
                                });
                    }
                })
                .openGallery();
    }
}
