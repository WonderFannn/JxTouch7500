package com.jinxin.jxtouchscreen.serialport;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.activity.LoginActivity;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.event.UpdateDataEvent;
import com.jinxin.jxtouchscreen.net.data.UpdateDataHandler;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by sail on 2017/4/26 0026.
 */
public class UpdataSerialPortService extends Service implements SerialPortUtil.OnDataReceiveListener{


    private static final String TAG = "UpdataSerialPortService";
    private static SerialPortUtil mSerialPortUtil = SerialPortUtil.getInstance();
    private UpdateDataSerialPortDecoder mUpdateDataSerialPortDecoder;
    public static boolean connectGateway = false;
    @Override
    public void onCreate() {
        super.onCreate();
        mSerialPortUtil.setOnDataReceiveListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("SerialPortUtil", "onStartCommand: ");/
//        mSerialPortUtil.setOnDataReceiveListener(this);
//        try {
//            mSerialPortUtil.sendBuffer(3,LocalConstant.QUERY_CONNET.getBytes("utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDataReceive(byte[] buffer, int index, int packlen){

        ToastUtil.showShort(BaseApp.getContext(),"接收到消息");
        byte[] reciveBuf = new byte[packlen];
        System.arraycopy(buffer, index, reciveBuf, 0, packlen);

        byte[] typeBytes = new byte[4];
        System.arraycopy(reciveBuf,2,typeBytes,0,4);
        int type = bytesToInt(typeBytes);
        int contentLength = packlen-12;
        byte[] content = new byte[contentLength];
        System.arraycopy(reciveBuf, 10, content, 0, contentLength);

        Log.d("SerialPortUtil","SerilPort message received type: "+type+" contentlenth: "+contentLength);
        switch (type){
            case 4:
            case 5:
                mUpdateDataSerialPortDecoder = new UpdateDataSerialPortDecoder(content);
                mUpdateDataSerialPortDecoder.startDecode();
                break;
            case 3:
                // 7500业务控制数据
                try {
                    String contentString = new String(content,"utf-8");
                    Log.d(TAG, "onDataReceive: "+contentString);
                    String response = contentString.substring(0,3);
                    String cmd = contentString.substring(3,contentString.length());
                    if (response.equals("200")){
                        if (cmd.equals(LocalConstant.QUERY_CONNET)){
                            ToastUtil.showShort(BaseApp.getContext(),"成功连接到安卓大网关");
                            connectGateway = true;
//                            if (isTopActivity("LoginActivity")) {
                                Log.d(TAG, "在登陆界面,开始更新数据");
                                EventBus.getDefault().post(new UpdateDataEvent());
//                            }
                        }else if (cmd.equals(LocalConstant.TRY_CONNET)){
                            ToastUtil.showShort(BaseApp.getContext(),"尝试连接大网关");
                        }else{
                            ToastUtil.showShort(BaseApp.getContext(),"已设置心跳频率");
                        }
                    }else if (response.equals("400")){
                        if (cmd.equals(LocalConstant.QUERY_CONNET)){
                            ToastUtil.showShort(BaseApp.getContext(),"未连接到安卓大网关");
                            //进行一次连接操作
                            try {
                                mSerialPortUtil.sendBuffer(3,LocalConstant.TRY_CONNET.getBytes("utf-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else if (cmd.equals(LocalConstant.TRY_CONNET)){
                            ToastUtil.showShort(BaseApp.getContext(),"尝试连接大网关失败");
                        }else{
                            ToastUtil.showShort(BaseApp.getContext(),"设置心跳频率被拒绝");
                        }
                    }else if (response.equals("201")){
                        ToastUtil.showShort(BaseApp.getContext(),"正在连接到大网关");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private int bytesToInt(byte[] typeBytes) {
            int sum = 0;
            for (byte b:typeBytes) {
                sum = (sum << 8) | (b & 0xff);
            }
            return sum;
    }

    public static boolean sendMessageByArea(String area) throws UnsupportedEncodingException {
        return mSerialPortUtil.sendMessageByArea(area);
    }

    private boolean isTopActivity(String activityName){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if(runningTaskInfos != null){
            cmpNameTemp = runningTaskInfos.get(0).topActivity.toString();
        }
        if(cmpNameTemp == null){
            return false;
        }
        return cmpNameTemp.equals(activityName);
    }

}
