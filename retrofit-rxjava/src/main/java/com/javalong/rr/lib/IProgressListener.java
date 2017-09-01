package com.javalong.rr.lib;

/**
 * Created by 令狐 on 17/8/31.
 */

public interface IProgressListener {
    void onProgressUpdated(long currentLength, long maxLength);
}
