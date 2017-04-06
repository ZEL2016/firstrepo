package com.zel.screenrecorder;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_COED_SCREEN_RECORD = 0x1;
    public static final int REQUEST_COED_CAN_DRAW_OVERLAYS = 0x2;
    private RecordManager mRecordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.hide();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_COED_CAN_DRAW_OVERLAYS);
                return;
            }
        }
        initPath();
        getCMD();
    }

    private void checkPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 0);
            }
        }
    }

    private void getCMD(){
        mRecordManager = RecordManager.getInstance(this);
        int cmd = getIntent().getIntExtra(RecordService.CMD, 0);
        switch (cmd) {
            case RecordService.CMD_START_RECORDING:
                if(mRecordManager.isRecording()){
                    finish();
                    return;
                }
                RecordService.startRecordService(this, RecordService.CMD_REMOVE_FLOAT_WINDOW);
                startActivityForResult(mRecordManager.getMediaProjectionManager().createScreenCaptureIntent(), REQUEST_COED_SCREEN_RECORD);
                break;
            default:
                if(!isServiceRunning("com.zel.screenrecorder.RecordService")){
                    RecordService.startRecordService(this, RecordService.CMD_SHOW_FLOAT_WINDOW);
                }
                finish();
        }
    }

    private void initPath(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "StorageState:" + Environment.getExternalStorageState(), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        File dir = new File(Environment.getExternalStorageDirectory() + "/ScreenRecord/");
        File dir_record = new File(Environment.getExternalStorageDirectory() + "/ScreenRecord/record/");
        File dir_capture = new File(Environment.getExternalStorageDirectory() + "/ScreenRecord/capture/");
        if(!dir.exists()){
            dir.mkdir();
        }
        if(!dir_record.exists()){
            dir_record.mkdir();
        }
        if(!dir_capture.exists()){
            dir_capture.mkdir();
        }
    }

    private boolean isServiceRunning(String service){
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(Integer.MAX_VALUE);
        for(ActivityManager.RunningServiceInfo info: list){
            if(service.equals(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i = 0;i < grantResults.length;i ++){
            Log.i("zhaoenlin", "permissions:" + permissions[i] + "---grantResults:" + grantResults[i]);
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                finish();
                return;
            }
        }
        getCMD();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_COED_SCREEN_RECORD:
                if(resultCode != RESULT_OK){
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    finish();
                    RecordService.startRecordService(this, RecordService.CMD_SHOW_FLOAT_WINDOW);
                    return;
                }

                RecordService.startRecordService(this, RecordService.CMD_START_RECORDING);
                mRecordManager.startRecord(resultCode, data);
                finish();
                break;
            case REQUEST_COED_CAN_DRAW_OVERLAYS:
                if(resultCode != RESULT_OK){
                    finish();
                    return;
                }
                checkPermissions();
                break;
        }

    }

}
