package com.jinxin.jxtouchscreen.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.MainActivity;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.LoginSuccessEvent;
import com.jinxin.jxtouchscreen.event.UpdateDataEvent;
import com.jinxin.jxtouchscreen.event.UpdateDataServiceDisconnectEvent;
import com.jinxin.jxtouchscreen.event.UpdateFailedEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.jpush.BroadPushMessage;
import com.jinxin.jxtouchscreen.jpush.MessageQueue2;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.serialport.SerialPortUtil;
import com.jinxin.jxtouchscreen.serialport.UpdataSerialPortService;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by XTER on 2017/01/11.
 * 登录
 */
public class LoginActivity extends BaseActivity {

	public static final int PERMISSION_CODE_ALERT = 12;
	public static final int PERMISSION_CODE_WRITE = 13;

	public static final int LOADING_SHOW = 0;
	public static final int LOADING_HIDE = 1;

	private Timer timer;

	private BaseUIHandler mHandler = new BaseUIHandler(this);

	private SerialPortUtil mSerialPortUtil;
	private boolean isSerialPort = true;

	@Override
	protected void initView() {
		mSerialPortUtil = SerialPortUtil.getInstance();
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_login);
	}

	@Override
	protected void initData() {
//		mSerialPortUtil.setOnDataReceiveListener(this);
		startService(new Intent(getApplicationContext(), UpdataSerialPortService.class));
		if (isSerialPort){
			updataWithSerialPort();

		}else {
			clearTimer();
			setTimerTask();
			mHandler.obtainMessage(LOADING_SHOW, getString(R.string.login_connecting)).sendToTarget();
			//6.0以上系统权限匹配
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.SYSTEM_ALERT_WINDOW)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
						PERMISSION_CODE_ALERT);
			}
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						PERMISSION_CODE_WRITE);
			}
		}
//		//获取root权限
//		Utils.get_root();
//		//判断系统文件下/system/lib中是否有libmsc.so文件 有则不处理 没有则 将assets中的文件copy进目录
//		boolean fileExit = Utils.fileIsExists("/system/lib/libmsc.so");
//		if (!fileExit){
//			Utils.CopyAssets(getApplicationContext(),"libmsc.so","/system/lib/libmsc.so");
//		}
	}

	private void updataWithSerialPort() {
			clearTimer();
			setTimerTask();
			mHandler.obtainMessage(LOADING_HIDE).sendToTarget();
			mHandler.obtainMessage(LOADING_SHOW, getString(R.string.login_connecting)).sendToTarget();
	}

	@Override
	protected void uiHandleMessage(Message msg) {
		switch (msg.what) {
			case LOADING_SHOW:
				showLoading((String) msg.obj);
				break;
			case LOADING_HIDE:
				hideLoading();
				break;
		}
	}

	/**
	 * 定时任务，发送广播获取网关地址
	 */
	private void setTimerTask() {
		if (timer == null)
			timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isSerialPort) {
					try {
						mSerialPortUtil.sendBuffer(3,LocalConstant.QUERY_CONNET.getBytes("utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else {
					pushMessageToGateway();
				}
			}
		}, 1000, 30 * 1000);
	}

	@Override
	public void onBackPressed() {
		if (timer == null) {
			AppUtil.exit();
		}
		clearTimer();
	}

	protected void clearTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onLoginDisconnected(UpdateDataServiceDisconnectEvent event) {
		//与大网关异常断开后，重置广播标记，停止数据更新服务，重置定时器
		BaseApp.IpFlag = true;
		stopService(new Intent(getApplicationContext(), UpdateDataService.class));
		clearTimer();
		setTimerTask();
		mHandler.obtainMessage(LOADING_HIDE).sendToTarget();
		mHandler.obtainMessage(LOADING_SHOW, getString(R.string.login_connecting)).sendToTarget();
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onLoginSuccess(LoginSuccessEvent event) {
		//得到网关地址，开始启动连接网关服务
		if (Build.VERSION.SDK_INT >= 23) {
			if (Settings.canDrawOverlays(this)) {
				//有悬浮窗权限开启服务绑定 绑定权限
				startService(new Intent(getApplicationContext(), UpdateDataService.class));
			} else {
				//没有悬浮窗权限,去开启悬浮窗权限
				try {
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
					startActivityForResult(intent, PERMISSION_CODE_ALERT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			//默认有悬浮窗权限  但是 华为, 小米,oppo等手机会有自己的一套Android6.0以下  会有自己的一套悬浮窗权限管理 也需要做适配
			mHandler.obtainMessage(LOADING_HIDE).sendToTarget();
			startService(new Intent(getApplicationContext(), UpdateDataService.class));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case PERMISSION_CODE_ALERT:
				if (Build.VERSION.SDK_INT >= 23) {
					if (!Settings.canDrawOverlays(this)) {
						ToastUtil.showShort(BaseApp.getContext(), "权限申请失败");
					} else {
						ToastUtil.showShort(BaseApp.getContext(), "权限申请成功");
						mHandler.obtainMessage(LOADING_HIDE).sendToTarget();
						startService(new Intent(getApplicationContext(), UpdateDataService.class));
					}
				}
				break;
			case PERMISSION_CODE_WRITE:
				break;
		}
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onUpdating(UpdateDataEvent event) {
		Log.d("EVENTBUS", "onUpdating: 更新数据");
		clearTimer();
		mHandler.obtainMessage(LOADING_HIDE).sendToTarget();
		mHandler.obtainMessage(LOADING_SHOW, getString(R.string.login_updating)).sendToTarget();
		//对大网关进行设备验证
		JSONObject jo = new JSONObject();
		jo.put("area", "identification");
		jo.put("time", SysUtil.getNow2());
		JSONObject jo1 = new JSONObject();
		WifiManager wm = (WifiManager)getSystemService(getApplicationContext().WIFI_SERVICE);
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
		jo1.put("uid",m_szWLANMAC + "222");
		jo1.put("type","02");
		jo.put("content",jo1);
		//上传到大网关
		UpdateDataService.updateSpecialData(jo);

		//网关连接就绪，开始更新数据
		if (isSerialPort) {
			//信息下发到7500串口
			try {
				Thread.sleep(1000);
				mSerialPortUtil.sendMessageByArea(LocalConstant.AREA_ALL);
				Log.d("EVENTBUS", "onUpdating: 串口发送更新数据");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			UpdateDataService.updateData(LocalConstant.AREA_ALL);
		}

	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onUpdating(UpdateFailedEvent event) {
		clearTimer();
		EventBus.getDefault().post(new UpdateDataEvent());
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUpdateData(UpdateFinishEvent event) {
		clearTimer();
		ToastUtil.showLong(BaseApp.getContext(), R.string.login_update_data_success);
		//数据更新完成
		mHandler.obtainMessage(LOADING_HIDE).sendToTarget();
		//跳转主界面
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

		finish();
	}

	/**
	 * 推送消息至大网关
	 */
	private void pushMessageToGateway() {
		try {
//			String id_account = SPM.getStr(SPM.CONSTANT,
//					LocalConstant.KEY_ACCOUNT, "");
			String id_account = "10003";
			// 先推送消息到大网关
			String msgconts = id_account + "getwayip" + "(3900)";
			MessageQueue2.getInstance().offer(
					new BroadPushMessage(msgconts, "account"));
			L.i(msgconts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		hideLoading();
	}

}
