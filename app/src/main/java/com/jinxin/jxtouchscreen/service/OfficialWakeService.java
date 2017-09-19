package com.jinxin.jxtouchscreen.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.activity.VoiceRecognitionActivity;
import com.jinxin.jxtouchscreen.broad.BroadcastManager;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class OfficialWakeService extends Service {
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    private int curThresh = 1 ;
    private boolean threadDisable = false;

    // 唤醒结果内容
    private String resultString;

    @Override
    public void onCreate() {
        Log.d("TAG", "OfficialWakeService created");
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastManager.ACTION_VOICE_WAKE);
        mFilter.addAction(BroadcastManager.ACTION_VOICE_WAKE_CLOSE);
        registerReceiver(wakeBroadcast, mFilter);
        // 初始化
        SpeechUtility.createUtility(this, "appid="+getString(R.string.app_id));

        //加载识唤醒地资源，resPath为本地识别资源路径
        StringBuffer param =new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(getApplicationContext(),
                RESOURCE_TYPE.assets, "ivw/"+getString(R.string.app_id)+".jet");
        param.append(SpeechConstant.IVW_RES_PATH+"="+resPath);
        param.append(","+ResourceUtil.ENGINE_START+"="+SpeechConstant.ENG_IVW);
        boolean ret = SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START, param.toString());
        if(!ret)
            Log.d("TAG", "启动本地引擎失败！");
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(this,null);
        initIvw();
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					try {
//						while(!threadDisable){
//							//每隔10秒 重新检测是否开启唤醒监听
//							Logger.debug(null, "wakewervice running");
//							Thread.sleep(10000);
//							if (mIvw != null && !mIvw.isListening()) {
//								mIvw.startListening(mWakeuperListener);
//							}
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//
//				}
//			}).start();
    }

    private void initIvw() {
        // 非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            resultString = "";
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            // 注意：传入的门限值个数不能大于资源所携带的唤醒词个数
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
            // mIvw.setParameter(SpeechConstant.IVW_THRESHOLD,"0:"+curThresh+";1:"+curThresh);
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            mIvw.startListening(mWakeuperListener);
        } else {
            ToastUtil.showShort(getApplicationContext(), "未初始化唤醒对象");
        }
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString =buffer.toString();
                Log.d("TAG", "WakeService:"+resultString);
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            ToastUtil.showShort(getApplicationContext(), "唤醒成功");
//				openApp(getPackageName(),getApplicationContext());
            openActivity();
        }

        @Override
        public void onError(SpeechError error) {
            ToastUtil.showShort(getApplicationContext(), "onError:"+error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d("TAG", "onBeginOfSpeech");
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

        }

        @Override
        public void onVolumeChanged(int arg0) {
            // TODO Auto-generated method stub
//				JxshApp.showToast(context, "当前正在说话，音量大小：" + arg0);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "onDestroy WakeService");
        unregisterReceiver(wakeBroadcast);
        threadDisable = true;//终止线程
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.stopListening();
            mIvw.destroy();
        } else {
            ToastUtil.showShort(getApplicationContext(), "not initialize");
        }
    }

    /**
     * 打开APP界面
     */
    private void openActivity(){
        Intent in = new Intent(getApplicationContext(),VoiceRecognitionActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 在receiver或者service里新建activity都要添加这个属性，
        // 使用addFlags,而不是setFlags
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除掉Task栈需要显示Activity之上的其他activity
        in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 加上这个才不会新建立一个Activity，而是显示旧的
        in.putExtra("type", 1);
        getApplicationContext().startActivity(in);
    }

    /**
     * 进入页面时获取云设置
     */
//    private void initAutoSend() {
//        CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getApplicationContext());
//
//        String _account = CommUtil.getCurrentLoginAccount();
//        if(isApplyMainSettings()) {
//            _account = CommUtil.getMainAccount();
//        }
//        List<CloudSetting> csList = new ArrayList<CloudSetting>();
//        csList.addAll(csDaoImpl.find(null, "customer_id=?", new String[]{_account}, null, null, null, null));
//        if (csList != null && csList.size() > 0) {
//            for (CloudSetting cs : csList) {
//                if (StaticConstant.PARAM_SEND_SWITCH.equals(cs.getCategory()) &&
//                        StaticConstant.PARAM_SEND_SWITCH.equals(cs.getItems())) {
//                    SharedDB.saveBooleanDB(CommUtil.getCurrentLoginAccount(), ControlDefine.KEY_VOICE_SEND_SWITCH, Boolean.valueOf(cs.getParams()));
//                }
//            }
//        }
//    }

//    private boolean isMainAccount() {
//        if(CommUtil.getMainAccount().equals(CommUtil.getCurrentLoginAccount())) {
//            return true;
//        }
//        return false;
//    }

//    private boolean isApplyMainSettings() {
//        if(!isMainAccount()) {
//            CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(getApplicationContext());
//            String _account = CommUtil.getMainAccount();
//            Log.d("TAG", _account);
//            List<CloudSetting> csList = csDaoImpl.find(null, "customer_id=?", new String[]{_account}, null, null, null, null);
//            for (CloudSetting cs : csList) {
//                if(StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getCategory()) &&
//                        StaticConstant.PARAM_APPLY_SUB_SWITCH.equals(cs.getItems())) {
//                    String applySubSwitch = cs.getParams();
//                    if (applySubSwitch != null) {
//                        return applySubSwitch.equals("1")?true:false;
//                    }
//                }
//            }
//            return false;
//        }
//        return true;
//    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver wakeBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastManager.ACTION_VOICE_WAKE.equals(action)) {
                if (mIvw != null) {
                    mIvw.startListening(mWakeuperListener);
                }
            }else if(BroadcastManager.ACTION_VOICE_WAKE_CLOSE.equals(action)){
                if (mIvw != null) {
                    mIvw.stopListening();
                }
            }
        }

    };
}
