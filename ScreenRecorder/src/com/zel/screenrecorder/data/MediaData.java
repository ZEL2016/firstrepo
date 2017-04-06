package com.zel.screenrecorder.data;

import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * Created by enlin.zhao on 2017/3/6.
 */

public class MediaData {

    public static final String DIR_VIDEO   = Environment.getExternalStorageDirectory() + "/ScreenRecord/record/";
    public static final String DIR_CAPTURE = Environment.getExternalStorageDirectory() + "/ScreenRecord/capture/";

    private String mName;
    private Drawable mDrawable;
    private String mSize;
    private String mDuration;
    private String mPath;

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
    }
}
