package com.zel.screenrecorder.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by enlin.zhao on 2017/3/6.
 */

public class MediaUtils {

    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

    public static Bitmap getVideoThumbnail(String videoPath,int width,int height,int kind) {
        Bitmap bitmap;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static String getFileSize(String path){
        long s;
        try {
            FileInputStream fis = new FileInputStream(path);
            s = fis.available();
        } catch (IOException e) {
            Log.i("zhaoenlin", "MediaUtils.e:" + e);
            s = 0;
        }
        String size = s / (1024 * 1024) + "MB";
        return size;
    }
}
