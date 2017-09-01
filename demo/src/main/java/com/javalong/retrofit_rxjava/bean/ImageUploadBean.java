package com.javalong.retrofit_rxjava.bean;

/**
 * Created by 令狐 on 17/8/31.
 */

public class ImageUploadBean {
    private String originPath;
    private int progress;

    public ImageUploadBean(String originPath, int progress) {
        this.originPath = originPath;
        this.progress = progress;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
