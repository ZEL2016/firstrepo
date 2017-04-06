package com.zel.screenrecorder;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by enlin.zhao on 2017/3/1.
 */

public class FloatWindowManager {

    public static FloatWindowManager instance;
    private Context mContext;
    private View mFloatView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private int statusBarHeight = -1;//48
    private int mCurrentX = 0;
    private int mCurrentY = 0;

    private FloatWindowManager(Context context){
        mContext = context;
        initFloatView();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();

        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }
    }

    private void initFloatView() {
        mFloatView = LayoutInflater.from(mContext).inflate(R.layout.float_window, null);
        FloatViewListener l = new FloatViewListener();

        mFloatView.findViewById(R.id.lay_record).setOnTouchListener(l);
        mFloatView.findViewById(R.id.lay_capture).setOnTouchListener(l);
        mFloatView.findViewById(R.id.lay_review).setOnTouchListener(l);
        mFloatView.findViewById(R.id.lay_setting).setOnTouchListener(l);

        mFloatView.findViewById(R.id.lay_record).setOnClickListener(l);
        mFloatView.findViewById(R.id.lay_capture).setOnClickListener(l);
        mFloatView.findViewById(R.id.lay_review).setOnClickListener(l);
        mFloatView.findViewById(R.id.lay_setting).setOnClickListener(l);

        mFloatView.findViewById(R.id.lay_close).setOnClickListener(l);

    }

    private class FloatViewListener implements View.OnTouchListener, View.OnClickListener {
        private float mTouchStartX, mTouchStartY;
        private float x, y;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            x = event.getRawX();
            y = event.getRawY() - statusBarHeight;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 获取相对View的坐标，即以此View左上角为原点
                    mTouchStartX = event.getX();
                    mTouchStartY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 更新浮动窗口位置参数
                    mCurrentX = (int) (x - mTouchStartX);
                    mCurrentY = (int) (y - mTouchStartY);
                    switch (v.getId()){
                        case R.id.lay_record:
                            break;
                        case R.id.lay_capture:
                            mCurrentX -= v.getWidth();
                            break;
                        case R.id.lay_review:
                            mCurrentX -= v.getWidth() * 2;
                            break;
                        case R.id.lay_setting:
                            mCurrentX -= v.getWidth() * 3;
                            break;
                    }
                    Log.i("zhaoenlin", "MotionEvent.ACTION_MOVE");
                    mParams.x = mCurrentX;
                    mParams.y = mCurrentY;
                    mWindowManager.updateViewLayout(mFloatView, mParams);
                    break;
                case MotionEvent.ACTION_UP:
                    // 更新浮动窗口位置参数
                    mCurrentX = (int) (x - mTouchStartX);
                    mCurrentY = (int) (y - mTouchStartY);
                    switch (v.getId()){
                        case R.id.lay_record:
                            break;
                        case R.id.lay_capture:
                            mCurrentX -= v.getWidth();
                            break;
                        case R.id.lay_review:
                            mCurrentX -= v.getWidth() * 2;
                            break;
                        case R.id.lay_setting:
                            mCurrentX -= v.getWidth() * 3;
                            break;

                    }
                    Log.i("zhaoenlin", "MotionEvent.ACTION_UP");
                    mParams.x = mCurrentX;
                    mParams.y = mCurrentY;
                    mWindowManager.updateViewLayout(mFloatView, mParams);
                    // 可以在此记录最后一次的位置
                    mTouchStartX = mTouchStartY = 0;
                    break;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            Log.i("zhaoenlin", "onClick");
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.lay_record:
                    intent.setClass(mContext, MainActivity.class);
                    intent.putExtra(RecordService.CMD, RecordService.CMD_START_RECORDING);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;
                case R.id.lay_capture:
                    break;
                case R.id.lay_review:
                    intent.setClass(mContext, ReviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;
                case R.id.lay_setting:
                    intent.setClass(mContext, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;
                case R.id.lay_close:
                    mContext.stopService(new Intent(mContext, RecordService.class));
                    mWindowManager.removeView(mFloatView);
                    break;
            }
        }
    }

    public static FloatWindowManager getInstance(Context context){
        synchronized (FloatWindowManager.class){
            if(instance == null){
                instance = new FloatWindowManager(context);
            }
        }
        return instance;
    }

    public void showFloatWindow(){
        float scale = mContext.getResources().getDisplayMetrics().density;
        Log.i("zhaoenlin", "scale:" + scale);
        int width = (int)mContext.getResources().getDimension(R.dimen.float_window_width);
        int height = (int)mContext.getResources().getDimension(R.dimen.float_window_height);
        Log.i("zhaoenlin", "width:" + width + "--height:" + height);
        mParams.width = width;
        mParams.height = height;
        //窗口图案放置位置
        mParams.gravity = Gravity.START | Gravity.TOP;
        // 如果忽略gravity属性，那么它表示窗口的绝对X位置。
        mParams.x = mCurrentX;
        //如果忽略gravity属性，那么它表示窗口的绝对Y位置。
        mParams.y = mCurrentY;
        //电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //FLAG_NOT_FOCUSABLE让window不能获得焦点，这样用户快就不能向该window发送按键事件及按钮事件
        //FLAG_NOT_TOUCH_MODAL即使在该window在可获得焦点情况下，仍然把该window之外的任何event发送到该window之后的其他window.
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 期望的位图格式。默认为不透明。参考android.graphics.PixelFormat。
        mParams.format = PixelFormat.RGBA_8888;

        mWindowManager.addView(mFloatView, mParams);
    }

    public void removeFloatWindow(){
        mWindowManager.removeView(mFloatView);
    }

}
