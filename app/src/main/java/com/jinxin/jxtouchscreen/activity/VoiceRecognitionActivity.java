package com.jinxin.jxtouchscreen.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.broad.BroadcastManager;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.UpdateDataServiceDisconnectEvent;
import com.jinxin.jxtouchscreen.event.UpdateFailedEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.util.FileManager;
import com.jinxin.jxtouchscreen.util.JsonParser;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;
import com.jinxin.jxtouchscreen.util.Utils;
import com.jinxin.jxtouchscreen.widget.VoiceService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yh on 2017/3/29.
 */

public class VoiceRecognitionActivity extends BaseActivity implements View.OnClickListener{

    private TextView mVoiceShowTextView;
    private TextView mVoiceToastTextView;
    private GifImageView mVoiceBtn;
    private TextView mTypeName;
    private ImageView mBackBtn;
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog iatDialog;
    //唤醒对象
    private VoiceWakeuper mIvw;

    private Context context;

    private int ret = 0;// 函数调用返回值
    private SoundPool soundPool = null;
    private Map<Integer, Integer> soundPoolMap;
    private AudioManager audioManager = null;
    private int sendCount = 0;//控制发送次数

    //周期执行时间
    private long mTime = 0;
    //是否处于识别周期
    private boolean isStaryPeriod = false;
    //是否处于休眠
    private boolean isActivate = false;
    //一次识别是否完成
    private boolean isComplete = false;

    private boolean isRunning = false;
    private MyThread mThread = null;
    //记录连续没有说话的次数
    private int count = 0;
    //切换的type
    private int type = 0; //1--------设备小助手  2------音乐小助手

    private BaseUIHandler mUIHander = new BaseUIHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (sendCount == 0) {
                        sendTextTask();
                        sendCount++;
                    }
                    break;
                case 2://开启语音识别
                    ret = mIat.startListening(recognizerListener);
                    if(ret != ErrorCode.SUCCESS){
                        Toast.makeText(context,getString(R.string.listen_fail) + ret ,Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,getString(R.string.text_begin) ,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3://完成听写，退出周期
                    isComplete = true;
                    isStaryPeriod = false;
                    break;
                case 4://上传大网关失败
                    mVoiceToastTextView.setText(context.getString(R.string.find_fail));
                    mVoiceShowTextView.setText(context.getString(R.string.need_help));
                    mUIHander.sendEmptyMessageDelayed(3,1500);
                    break;
                case 5://上传大网关成功
                    mVoiceToastTextView.setText(context.getString(R.string.find_success));
                    mVoiceShowTextView.setText(context.getString(R.string.need_help));
                    mUIHander.sendEmptyMessageDelayed(3,1500);
                    break;
                case 6://退出界面
                    BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void initView() {
        type = getIntent().getIntExtra("type",1);
        //停止悬浮窗服务
        stopService(new Intent(getApplicationContext(), VoiceService.class));
        //停止唤醒服务
        BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE_CLOSE, null);
        EventBus.getDefault().register(this);
        setContentView(R.layout.voice_layout);
        mTypeName = (TextView) findViewById(R.id.type_name);
        mVoiceShowTextView = (TextView) findViewById(R.id.voice_show_text);
        mVoiceToastTextView = (TextView) findViewById(R.id.voice_toast_text);
        mVoiceBtn = (GifImageView) findViewById(R.id.voice_btn);
        mBackBtn = (ImageView) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
//        mVoiceBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        context = this;
        //初始化识别周期逻辑控制变量
        isActivate = true;
        isRunning = true;
        isComplete = true;
        mThread = new MyThread();

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(1, soundPool.load(this, R.raw.ring_success, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.ring_error, 1));

        // 初始化识别对象
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        // 设置语音识别参数
        setParam();

        // 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
        iatDialog = new RecognizerDialog(context,mInitListener);
        // 初始化语音唤醒对象
        mIvw = VoiceWakeuper.getWakeuper();
        if (type == 1){
            mTypeName.setText(context.getString(R.string.equipment_assistant));
        }else if(type == 2){
            mTypeName.setText(context.getString(R.string.music_assistant));
        }
    }

    class MyThread extends Thread {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    if (!isStaryPeriod) {
                        isStaryPeriod = true;
                        openSpeechRecognisePeriod();
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 开启识别周期
     */
    private void openSpeechRecognisePeriod(){
        if (!mIat.isListening()) {
            mUIHander.sendEmptyMessage(2);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.voice_btn:

                break;
            case R.id.back_btn:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            L.d(null, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(context,getString(R.string.init_fail) + code ,Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 参数设置
     * @return
     */
    public void setParam(){
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
//		String lag = mSharedPreferences.getString("iat_language_preference", "mandarin");
        String lag = "zh_cn";
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        }else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT,lag);
        }
        // 设置语音前端点
        mIat.setParameter(SpeechConstant.VAD_BOS, SPM.getStr(SPM.ORDINARY_CONSTANTS, "iat_vadbos_preference", "5000"));
        // 设置语音后端点
        mIat.setParameter(SpeechConstant.VAD_EOS, SPM.getStr(SPM.ORDINARY_CONSTANTS,"iat_vadeos_preference", "2000"));
        // 设置标点符号 "1": 带标点,  "0"： 不带标点
        mIat.setParameter(SpeechConstant.ASR_PTT, SPM.getStr(SPM.ORDINARY_CONSTANTS,"iat_punc_preference", "0"));
        // 设置音频保存路径
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, new FileManager().getVoicePath());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThread != null && !mThread.isAlive()) {
            mThread.start();
        }
    }

    /**
     * 播放提示音
     * @param id
     */
    private void playSound(int id){
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float voice = (float) 0.7 / max * current;
        soundPool.play(id, voice, voice, 0, 0, 1f);
    }
    /**
     * 听写监听器。
     */
    private RecognizerListener recognizerListener=new RecognizerListener(){

        @Override
        public void onBeginOfSpeech() {
            Log.e("Tag", "onBeginOfSpeech");
            mVoiceShowTextView.setText(context.getString(R.string.need_help));
            isComplete = false;
//            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            GifDrawable gifDrawable = null;
            try {
                gifDrawable = new GifDrawable(getResources(), R.drawable.voice_d);
                // gif1加载一个动态图gif
                mVoiceBtn.setImageDrawable(gifDrawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(SpeechError error) {
            Log.e("Tag", "onError");
            count++;
            if (count == 10){
                mVoiceToastTextView.setText(context.getString(R.string.exit_voice));
                mUIHander.sendEmptyMessageDelayed(6,500);
            }else{
                mVoiceBtn.setImageResource(R.drawable.voice_n);
                mVoiceBtn.invalidate();
                mVoiceToastTextView.setText(context.getString(R.string.voice_refresh));
                mVoiceShowTextView.setText(context.getString(R.string.recognition_fail));
                mUIHander.sendEmptyMessageDelayed(3,1000);
            }

        }

        @Override
        public void onEndOfSpeech() {
            Log.e("Tag", "onEndOfSpeech");

            isComplete = true;
            mVoiceToastTextView.setText(context.getString(R.string.wait_reading));
//            Toast.makeText(context,getString(R.string.wait_reading),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            String str = mVoiceShowTextView.getText().toString();
            if(str.contains(context.getString(R.string.need_help))){
                str = str.replace(context.getString(R.string.need_help),"");
            }
            mVoiceShowTextView.setText(str);
            mVoiceShowTextView.append(text);
            String result = mVoiceShowTextView.getText().toString();
            Log.e("Tag", "onResult:"+ result);
            if(isLast) {
                //TODO 最后的结果
                sendCount = 0;
                mUIHander.sendEmptyMessage(1);
            }
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

        }


        @Override
        public void onVolumeChanged(int arg0, byte[] arg1) {
            mVoiceToastTextView.setText(context.getString(R.string.talking_voice));
//            Toast.makeText(context, getString(R.string.speak_volume) + arg0,Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "VRA->onDestroy");
        isRunning = false;
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
        // 关闭当前的唤醒监听
        if(mIvw!=null)
            mIvw.stopListening();
        if (mThread.isAlive()) {
            mThread.interrupt();
        }
        BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
        soundPool.release();
        EventBus.getDefault().unregister(this);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /**
     * 传送文本数据到大网关
     */
    private void sendTextTask() {
        mVoiceBtn.setImageResource(R.drawable.voice_n);
        mVoiceBtn.invalidate();
        String content = mVoiceShowTextView.getText().toString();
        String contentPinyin = Utils.converterToSpell(content);
        String exitPinyin = Utils.converterToSpell(context.getString(R.string.btn_exit));
        String changeEquPinyin = Utils.converterToSpell(context.getString(R.string.equipment));
        String[] changeMusicPinyin = Utils.converterToSpell(context.getString(R.string.music)).split(",");
        if (!TextUtils.isEmpty(content) ){
            if(content.equals("。")){
                //相当于没有说话
                count++;
                if (count == 10){
                    mVoiceToastTextView.setText(context.getString(R.string.exit_voice));
                    mUIHander.sendEmptyMessageDelayed(6,500);
                }else{
                    mVoiceToastTextView.setText(context.getString(R.string.voice_refresh));
                    mVoiceShowTextView.setText(context.getString(R.string.voice_null));
                    mUIHander.sendEmptyMessageDelayed(3,1000);
                }
            }else if(contentPinyin.contains(exitPinyin)){
                //此时表示退出语音识别界面
                mVoiceToastTextView.setText(context.getString(R.string.exit_voice));
                mUIHander.sendEmptyMessageDelayed(6,500);
            }else if (contentPinyin.contains(changeEquPinyin)){
                type = 1;
                mTypeName.setText(context.getString(R.string.equipment_assistant));
                mVoiceToastTextView.setText(context.getString(R.string.change_mode));
                mUIHander.sendEmptyMessageDelayed(3,500);
            }else if (contentPinyin.contains(changeMusicPinyin[0])||contentPinyin.contains(changeMusicPinyin[1])){
                type = 2;
                mTypeName.setText(context.getString(R.string.music_assistant));
                mVoiceToastTextView.setText(context.getString(R.string.change_mode));
                mUIHander.sendEmptyMessageDelayed(3,500);
            }else{
                //此时表示正在说话,将之前的计数清空
                if (count > 0){
                    count = 0;
                }
                mVoiceToastTextView.setText(context.getString(R.string.uploading_voice));
//            //通过长连接传送至大网关
                JSONObject jo = new JSONObject();
                jo.put("area", "voice_upload");
                jo.put("time", SysUtil.getNow2());
                JSONObject jo1 = new JSONObject();
                jo1.put("order", SysUtil.getUTF8XMLString(content));
                jo1.put("type",type);
                jo.put("content",jo1);
                //上传到大网关
                UpdateDataService.updateSpecialData(jo);
            }

        }else{
            //此时没有说话
            count++;
            if (count == 10 ){
                mVoiceToastTextView.setText(context.getString(R.string.exit_voice));
                mUIHander.sendEmptyMessageDelayed(6,500);
            }else {
                mVoiceToastTextView.setText(context.getString(R.string.voice_refresh));
                mVoiceShowTextView.setText(context.getString(R.string.voice_null));
                mUIHander.sendEmptyMessageDelayed(3,1000);
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateVoiceFailed(UpdateFailedEvent event) {
        //上传网关失败
        playSound(2);
        Message msg = new Message();
        msg.what = 4;
        mUIHander.sendMessage(msg);

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateVoiceFinish(UpdateFinishEvent event) {
        //上传到大网关成功
        playSound(1);
        Message msg = new Message();
        msg.what = 5;
        mUIHander.sendMessage(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisconnect(UpdateDataServiceDisconnectEvent event) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(BaseApp.isBackground(getApplicationContext())){
            isRunning = false;
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
            // 关闭当前的唤醒监听
            mIvw.stopListening();
            if (mThread.isAlive()) {
                mThread.interrupt();
            }
            BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE, null);
            soundPool.release();
            EventBus.getDefault().unregister(this);
            this.finish();
        }
    }

}
