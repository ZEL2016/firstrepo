package com.zel.screenrecorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by enlin.zhao on 2017/3/1.
 */

public class RecordManager {
    public static final String DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScreenRecord/record/";/*"/sdcard/capture.mp4"*/

    private Context mContext;
    private static RecordManager mRecordManager;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private MediaProjectionCallback mMediaProjectionCallback;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mRecorder;
    private boolean isRecording;
    private int mWidth;
    private int mHeight;
    private int mDpi;

    private RecordManager(Context context){
        mContext = context;
        getScreenSize();
        mRecorder = new MediaRecorder();
        mMediaProjectionManager = (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMediaProjectionCallback = new MediaProjectionCallback();
    }

    public static RecordManager getInstance(Context context){
        synchronized (RecordManager.class) {
            if(mRecordManager == null){
                mRecordManager = new RecordManager(context);
            }
        }
        return mRecordManager;
    }

    public void startRecord(int resultCode, Intent data){
        initRecorder(DIR + System.currentTimeMillis() + ".mp4");
        isRecording = true;
        mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("SCREEN_RECORDER", mWidth, mHeight, mDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mRecorder.getSurface(), null, null);
        mRecorder.start();
    }

    public void stopRecord(){
        isRecording = false;
        Log.i("zhaoenlin", "stopRecord");
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mRecorder.stop();
            mRecorder.reset();
            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
                mVirtualDisplay = null;
            }

        }
    }

    private void getScreenSize() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight= dm.heightPixels;
        mDpi = dm.densityDpi;
    }

    private void initRecorder(String path) {
        /**
         *  视频编码格式：default，H263，H264，MPEG_4_SP
         获得视频资源：default，CAMERA
         音频编码格式：default，AAC，AMR_NB，AMR_WB
         获得音频资源：defalut，camcorder，mic，voice_call，voice_communication,
         voice_downlink,voice_recognition, voice_uplink
         输出方式：amr_nb，amr_wb,default,mpeg_4,raw_amr,three_gpp
         */
        //设置音频源
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频源：Surface和Camera 两种
        mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //设置视频输出格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置视频编码格式
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置音频编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置视频编码的码率
        mRecorder.setVideoEncodingBitRate(5000 * 1000);
        //设置视频编码的帧率
        mRecorder.setVideoFrameRate(30);
        //设置视频尺寸大小
        mRecorder.setVideoSize(mWidth, mHeight);
        //设置视频输出路径
        mRecorder.setOutputFile(path);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.i("zhaoenlin", "IOException:" + e);
        }
    }

    public MediaProjectionManager getMediaProjectionManager(){
        return mMediaProjectionManager;
    }

    public boolean isRecording(){
        return isRecording;
    }
}
