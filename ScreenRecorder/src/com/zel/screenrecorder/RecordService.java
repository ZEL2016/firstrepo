package com.zel.screenrecorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by enlin.zhao on 2017/3/1.
 */

public class RecordService extends Service {

    public static final String CMD = "COMMAND";
    public static final int CMD_START_RECORDING      = 1;
    public static final int CMD_STOP_RECORDING       = 2;
    public static final int CMD_SHOW_FLOAT_WINDOW    = 3;
    public static final int CMD_REMOVE_FLOAT_WINDOW  = 4;

    private RecordManager mRecordManager;
    FloatWindowManager mFloatWindowManager;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CMD_START_RECORDING:
                    showRecordingNotification();
                    break;
                case CMD_STOP_RECORDING:
                    mRecordManager.stopRecord();
                    mFloatWindowManager.showFloatWindow();
                    break;
                case CMD_SHOW_FLOAT_WINDOW:
                    mFloatWindowManager.showFloatWindow();
                    break;
                case CMD_REMOVE_FLOAT_WINDOW:
                    mFloatWindowManager.removeFloatWindow();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mRecordManager = RecordManager.getInstance(this);
        mFloatWindowManager = FloatWindowManager.getInstance(RecordService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cmd = 0;
        if(intent != null){
            cmd = intent.getIntExtra(CMD, 0);
        }
        mHandler.sendEmptyMessage(cmd);
        Log.i("zhaoenlin", "cmd:"+ cmd);
        /*switch (cmd) {
            case CMD_START_RECORDING:
                showRecordingNotification();
                mFloatWindowManager.removeFloatWindow();
                break;
            case CMD_STOP_RECORDING:
                mRecordManager.stopRecord();
                mFloatWindowManager.showFloatWindow();
                break;
            case CMD_SHOW_FLOAT_WINDOW:
                mFloatWindowManager.showFloatWindow();
                break;
            case CMD_REMOVE_FLOAT_WINDOW:
                mFloatWindowManager.removeFloatWindow();
                break;
        }*/

        return super.onStartCommand(intent, flags, startId);
    }

    public static void startRecordService(Context context,int cmd){
        Intent service = new Intent(context, RecordService.class);
        service.putExtra(RecordService.CMD, cmd);
        context.startService(service);
    }

    private void showRecordingNotification(){
        Log.i("zhaoenlin", "showRecordingNotification");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent service = new Intent(this, RecordService.class);
        service.putExtra(CMD, CMD_STOP_RECORDING);
        PendingIntent pi = PendingIntent.getService(this, 0, service, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.drawable.ic_recording)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .build();
        manager.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("zhaoenlin", "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
