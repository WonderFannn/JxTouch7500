package com.jinxin.jxtouchscreen.widget;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.activity.VoiceRecognitionActivity;
import com.jinxin.jxtouchscreen.activity.VoiceRecognitionActivity1;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

import static com.jinxin.jxtouchscreen.MainActivity.currentWakeMode;

/**
 * Created by yh on 2017/4/6.
 */

public class VoiceService extends Service {

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    Button mFloatView;
    // 声明屏幕的宽高
    float x, y;
    private static final String TAG = "VoiceService";

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createFloatView()
    {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = SysUtil.getScreenWidth();
        wmParams.y = SysUtil.getScreenHeight() / 2 - 150;

        //设置悬浮窗口长宽数据
        wmParams.width = 80;
        wmParams.height = 80;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.voice_service_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = (Button)mFloatLayout.findViewById(R.id.voice_btn);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                        wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight();
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                    case MotionEvent.ACTION_UP:
//                        balancePos(wmParams);
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        break;
                }

                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        mFloatView.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(currentWakeMode){
                    Intent intent = new Intent(getApplicationContext(), VoiceRecognitionActivity1.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("type",1);
                    getApplication().startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), VoiceRecognitionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("type",1);
                    getApplication().startActivity(intent);
                }
            }
        });

    }
    private void balancePos(WindowManager.LayoutParams wmParams) {
        int distanceX = Math.abs(wmParams.x - SysUtil.getScreenWidth());
        int distanceY = Math.abs(wmParams.y - SysUtil.getScreenHeight());
        if (distanceX <= distanceY) {
            if (wmParams.x > SysUtil.getScreenWidth() / 2) {
                wmParams.x = SysUtil.getScreenWidth() - mFloatView.getMeasuredWidth() / 2;
            } else {
                wmParams.x = 0;
            }
        } else {
            if (wmParams.y > SysUtil.getScreenHeight() / 2) {
                wmParams.y = SysUtil.getScreenHeight() - mFloatView.getMeasuredHeight() / 2;
            } else {
                wmParams.y = 0;
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        L.w("停止数据更新服务");
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }}
