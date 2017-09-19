package com.jinxin.jxtouchscreen.fragment.system;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.activity.LoginActivity;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.broad.BroadcastManager;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.NoneEvent;
import com.jinxin.jxtouchscreen.event.UpdateDataServiceDisconnectEvent;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.serialport.SerialPortUtil;
import com.jinxin.jxtouchscreen.serialport.UpdataSerialPortService;
import com.jinxin.jxtouchscreen.service.CustomWakeService;
import com.jinxin.jxtouchscreen.service.OfficialWakeService;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.FileManager;
import com.jinxin.jxtouchscreen.util.FileUtil;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.widget.MessageBox;
import com.jinxin.jxtouchscreen.widget.VoiceService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.jinxin.jxtouchscreen.activity.SettingActivity.cleanDatabases;
import static com.jinxin.jxtouchscreen.activity.SettingActivity.cleanExternalCache;
import static com.jinxin.jxtouchscreen.activity.SettingActivity.cleanFiles;
import static com.jinxin.jxtouchscreen.activity.SettingActivity.cleanInternalCache;
import static com.jinxin.jxtouchscreen.activity.SettingActivity.cleanSharedPreference;

/**
 * 系统设置界面
 * Created by HJK on 2017/4/11 0011.
 */
public class SystemSettingFragment extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox custom_wake_switch;
    private Boolean isCustomWake;
    private MessageBox _mBox;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    private EditText et_heartrate;
    private Button bt_set_heartrate;

    private SerialPortUtil mSerialPortUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
        EventBus.getDefault().register(this);
        initData();
    }

    private void initData() {
        isCustomWake = SPM.getBoolean(AppUtil.getCurrentAccount(), NetConstant.KEY_WAKE_MODE, false);
        mSerialPortUtil = SerialPortUtil.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_system_setting, container, false);
        initView(rootView);
        return rootView;
    }

    protected void initView(View view) {
        custom_wake_switch = (CheckBox) view.findViewById(R.id.custom_wake_switch);
        custom_wake_switch.setChecked(isCustomWake);
        custom_wake_switch.setOnCheckedChangeListener(this);
        view.findViewById(R.id.setting_clear_cache).setOnClickListener(this);
        view.findViewById(R.id.iv_backbtn_system).setOnClickListener(this);
        et_heartrate = (EditText) view.findViewById(R.id.editText_heartrate);
        bt_set_heartrate = (Button) view.findViewById(R.id.button_setheartrate);
        bt_set_heartrate.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.custom_wake_switch:
                //点击切换自定义唤醒之后弹出重登录提示框
//                showReloginBox(isChecked);
                boolean currentWakeMode = SPM.getBoolean(AppUtil.getCurrentAccount(), NetConstant.KEY_WAKE_MODE, false);
                if(currentWakeMode){
                    //关闭自定义唤醒
                    Log.d("TAG", "关闭自定义唤醒");
                    final Intent intent = new Intent(getContext(), CustomWakeService.class);
                    BroadcastManager.sendBroadcast(BroadcastManager.ACTION_VOICE_WAKE_CLOSE, null);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getContext().stopService(intent);
                        }
                    },200);

                }else{
                    //关闭官方唤醒
                    Log.d("TAG", "关闭官方唤醒");
                    getContext().stopService(new Intent(getContext(), OfficialWakeService.class));
                }
                if(isChecked) {
                    SPM.saveBoolean(AppUtil.getCurrentAccount(), NetConstant.KEY_WAKE_MODE, true);
                    Log.d("TAG", "启动自定义唤醒");
                    getContext().startService(new Intent(getContext(), CustomWakeService.class));
                }else {
                    SPM.saveBoolean(AppUtil.getCurrentAccount(), NetConstant.KEY_WAKE_MODE, false);
                    Log.d("TAG", "启动官方唤醒");
                    getContext().startService(new Intent(getContext(), OfficialWakeService.class));
                }
                Toast.makeText(getContext(),getContext().getString(R.string.change_wake_style),Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showReloginBox(boolean isChecked) {
        if(_mBox == null){
            _mBox = new MessageBox(getActivity(), "提示", "已切换唤醒方式，重启后生效。",
                    MessageBox.MB_OK | MessageBox.MB_CANCEL);
            _mBox.setButtonText("立即重启", "稍后重启");
        }
        if(!_mBox.isShowing()){
            _mBox.show();
        }
        if(isChecked) {
            SPM.saveBoolean(AppUtil.getCurrentAccount(), NetConstant.KEY_WAKE_MODE, true);
        }else {
            SPM.saveBoolean(AppUtil.getCurrentAccount(), NetConstant.KEY_WAKE_MODE, false);
        }

        _mBox.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                switch(_mBox.getResult()){
                    case MessageBox.MB_OK://点击消失
                        _mBox.dismiss();
                        //重新登录
                        AppUtil.exit();
//                        AppUtil.restartApp();
                        break;
                    case MessageBox.MB_CANCEL:
                        _mBox.dismiss();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_clear_cache:    //清除缓存
                cleanApplicationData(BaseApp.getContext());
                /*刷新主页数据*/
                delectCache();
                DBM.clearAccout();
                break;
            case R.id.iv_backbtn_system:   //退出键
                getDialog().dismiss();
                break;
            case R.id.button_setheartrate:
                setHeartrate();
                break;
        }
    }

    private void setHeartrate() {
        final String heartrateString = et_heartrate.getText().toString();
        if (!heartrateString.isEmpty() && isInteger(heartrateString)){
            int heartrate = Integer.valueOf(heartrateString);
            if( heartrate >= 1 && heartrate <= 40) {
                try {
                    String cmd = LocalConstant.SET_HEARTRATE + "," + heartrate*1000;
                    mSerialPortUtil.sendBuffer(3, cmd.getBytes("utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else {
                ToastUtil.showShort(BaseApp.getContext(),"请输入1-40内的数字");
            }
        }else {
            ToastUtil.showShort(BaseApp.getContext(),"请输入正确的数字");
        }

    }
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    /**
     * 清除本应用所有的数据 * * @param context * @param filepath
     */
    public void cleanApplicationData(Context context) {
        SPM.removeSP(SPM.CONFIGURATION);
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        System.gc();
        ToastUtil.showShort(BaseApp.getInstance().getApplicationContext(), "清除成功");
    }

    /**
     * 删除本地缓存文件和数据
     */
    private void delectCache() {
        Map<String, Object> veinMap = backUpVeinSettings();

        SPM.removeSP(AppUtil.getCurrentAccount());//删除配置表
        DBM.getCurrentOrm().deleteDatabase();//删除本地数据库
        FileManager _fm = new FileManager();
        if (_fm.getSDPath() != null) {
            final String filePath = _fm.getSDPath() + FileManager.PROJECT_NAME + FileManager.CACHE;
            FileUtil.checkDirectory(filePath);
            try {//清文件缓存
				/*BaseApp.getInstance().getFinalBitmap().clearCache();//清图片缓存
				BaseApp.getInstance().getFinalBitmap().clearMemoryCache();
				BaseApp.getInstance().getFinalBitmap().clearDiskCache();*/
                FileUtil.delFiles(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //BaseApp.closeLoading();

        }
        ToastUtil.showShort(BaseApp.getInstance().getApplicationContext(), "缓存已清除");
        restoreVeinSettings(veinMap);
    }

    private Map<String, Object> backUpVeinSettings() {
        String _account = AppUtil.getCurrentAccount();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(NetConstant.KEY_ENABLE_PASSWORD_VEIN, SPM.getBoolean(_account, NetConstant.KEY_ENABLE_PASSWORD_VEIN, false));
        map.put(NetConstant.KEY_VEIN_ONLY, SPM.getBoolean(_account, NetConstant.KEY_VEIN_ONLY, false));
        map.put(NetConstant.KEY_PASSWORD, SPM.getStr(_account, NetConstant.KEY_PASSWORD, ""));
        return map;
    }

    private void restoreVeinSettings(Map<String, Object> map) {
        String _account = AppUtil.getCurrentAccount();
        SPM.saveBoolean(_account, NetConstant.KEY_ENABLE_PASSWORD_VEIN, (Boolean) map.get(NetConstant.KEY_ENABLE_PASSWORD_VEIN));
        SPM.saveBoolean(_account, NetConstant.KEY_VEIN_ONLY, (Boolean) map.get(NetConstant.KEY_VEIN_ONLY));
        SPM.saveStr(_account, NetConstant.KEY_PASSWORD, (String) map.get(NetConstant.KEY_PASSWORD));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdate(NoneEvent event){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
