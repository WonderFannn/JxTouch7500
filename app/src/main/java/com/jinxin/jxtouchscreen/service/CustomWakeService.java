package com.jinxin.jxtouchscreen.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.activity.VoiceRecognitionActivity1;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.broad.BroadcastManager;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.WakeWord;
import com.jinxin.jxtouchscreen.util.JsonParser;
import com.jinxin.jxtouchscreen.util.PinyinFormat;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class CustomWakeService extends Service {

    // 语音听写对象
    public static SpeechRecognizer hearer;
    //测试数据
    static List<WakeWord> wakewords ;
    ArrayList<String> data_list = new ArrayList<String>();
    //唤醒监听线程
    WakeThread mWakeThread;

    @Override
    public void onCreate() {
        Log.d(null, "CustomWakeService created");
        super.onCreate();

        wakewords = DBM.loadWakeWordFromDb();
        for(int i=0; i<wakewords.size(); i++){
            data_list.add(wakewords.get(i).getWakeUpWord());
        }
        Log.i("TAG", "当前唤醒词有:"+data_list);

        //注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastManager.ACTION_VOICE_WAKE);
        mFilter.addAction(BroadcastManager.ACTION_VOICE_WAKE_CLOSE);
        mFilter.addAction(BroadcastManager.ACTION_VOICE_WAKE_AGAIN);
        registerReceiver(wakeBroadcast, mFilter);

        // 初始化
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
        init();
    }

    private void init() {
        hearer = SpeechRecognizer.createRecognizer(this, mInitListener);
        setParameter();
        // 非空判断，防止因空指针使程序崩溃
        if (hearer != null) {
            BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
        } else {
            ToastUtil.showShort(getApplicationContext(), "未初始化唤醒对象");
        }
    }

    /**
     * 设置听写对象
     */
    private void setParameter() {
        // domain:域名
        hearer.setParameter(SpeechConstant.DOMAIN, "iat");
        // zh_cn汉语
        hearer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // mandarin:普通话
        hearer.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        hearer.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        hearer.setParameter(SpeechConstant.VAD_EOS, "800");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        hearer.setParameter(SpeechConstant.ASR_PTT,  "0");
    }

    public class WakeThread extends Thread{
        Boolean isJoinActivity;
        public WakeThread(Boolean isJoinActivity) {
            this.isJoinActivity = isJoinActivity;
        }
        @Override
        public void run() {
            super.run();
            try {
                if(isJoinActivity ){
                    //进入了听写界面
                }else if(!isJoinActivity){//没进入听写界面
                    while(!BaseApp.getRunningActivityName().equals("com.jinxin.jxtouchscreen.activity.VoiceRecognitionActivity1")){
                        if (hearer != null && !hearer.isListening()) {
                            Thread.sleep(300);
                            hearer.startListening(mRecoListener);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Log.i("TAG", "初始化失败，错误码：" + i);
            }
        }
    };

    /**
     * 听写监听器
     */
    private RecognizerListener mRecoListener = new RecognizerListener(){
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            parsedResult(results, isLast);
        }

        @Override
        public void onBeginOfSpeech() {
            //开始录音
            Log.i("TAG", "开始录音!");
        }

        @Override
        public void onEndOfSpeech() {
            Log.i("TAG", "结束录音!");
        }

        @Override
        public void onError(SpeechError error) {
            if(error.getErrorCode()!=10118){
                ToastUtil.showShort(getApplicationContext(), "onError:"+error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

        @Override
        public void onVolumeChanged(int i, byte[] b) {

        }
    };

    /**
     * 解析json并显示听写内容（执行唤醒内容）
     * @param results
     * 			json数据
     * @param isLast
     * 			是否是最后一句话
     */
    private void parsedResult(RecognizerResult results, Boolean isLast) {

        String text = null;
        Log.i("TAG", "parsedJsonAndSetText：" + results.getResultString());
        try {
            text = JsonParser.parseIatResult(results.getResultString());
            if(TextUtils.isEmpty(text)){
                return;
            }
            Log.i("TAG", "WS->result:"+text);

            if(!TextUtils.isEmpty(text) && data_list != null){
                int i = 0;
                for(String word : data_list){
                    if(PinyinFormat.getPinYin(text).contains(
                            PinyinFormat.getPinYin(word))){
                        i++;
                    }
                }
                if(i>0){
                    Log.i("TAG", "小宇宙被唤醒了!!!!!");
                    openActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开APP界面
     */
    private void openActivity(){
        Intent in = null;
        in = new Intent(getApplicationContext(),VoiceRecognitionActivity1.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 在receiver或者service里新建activity都要添加这个属性，
        // 使用addFlags,而不是setFlags
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除掉Task栈需要显示Activity之上的其他activity
        in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 加上这个才不会新建立一个Activity，而是显示旧的
        in.putExtra("type", 1);
        getApplicationContext().startActivity(in);
        //		hearer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 唤醒广播
     */
    private BroadcastReceiver wakeBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastManager.ACTION_VOICE_WAKE.equals(action)) {
                if (hearer != null) {
                    hearer.startListening(mRecoListener);
                }else {
                    hearer = SpeechRecognizer.createRecognizer(context, mInitListener);
                    hearer.startListening(mRecoListener);
                }
                mWakeThread = new WakeThread(false);
                mWakeThread.start();
            }else if(BroadcastManager.ACTION_VOICE_WAKE_AGAIN.equals(action)){
                if (hearer != null) {
                    hearer.startListening(mRecoListener);
                }else {
                    hearer = SpeechRecognizer.createRecognizer(context, mInitListener);
                    hearer.startListening(mRecoListener);
                }
                mWakeThread = new WakeThread(true);
                mWakeThread.start();
            }else if(BroadcastManager.ACTION_VOICE_WAKE_CLOSE.equals(action)){
                if(mWakeThread!=null){
                    mWakeThread.interrupt();
                    mWakeThread = null;
                }
                if (hearer != null) {
                    hearer.stopListening();
                    hearer = null;
                }
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wakeBroadcast);
    }
}
