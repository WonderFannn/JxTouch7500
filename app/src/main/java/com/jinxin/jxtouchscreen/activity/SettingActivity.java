package com.jinxin.jxtouchscreen.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.base.BaseActivity;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.SetupActivityLoadEvent;
import com.jinxin.jxtouchscreen.event.UpdateDataServiceDisconnectEvent;
import com.jinxin.jxtouchscreen.event.UpdateFinishEvent;
import com.jinxin.jxtouchscreen.fragment.devicenispection.DeviceInspectionFragment;
import com.jinxin.jxtouchscreen.fragment.messagelist.MessageListFragment;
import com.jinxin.jxtouchscreen.fragment.system.SystemSettingFragment;
import com.jinxin.jxtouchscreen.net.data.UpdateDataService;
import com.jinxin.jxtouchscreen.serialport.SerialPortUtil;
import com.jinxin.jxtouchscreen.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by XTER on 2017/01/17.
 * 设置页面
 */
public class SettingActivity extends BaseActivity {

	private SerialPortUtil mSerialPortUtil;
	@Bind(R.id.tv_setting_account_id)
	TextView tvAccount;
	MessageListFragment messageListFragment;
	DeviceInspectionFragment deviceInspectionFragment;
	SystemSettingFragment systemSettingFragment;

	@Override
	protected void initView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_setting);
	}

	@Override
	protected void initData() {
		mSerialPortUtil = SerialPortUtil.getInstance();
		tvAccount.setText(SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, ""));
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinish(UpdateDataServiceDisconnectEvent event){
		finish();
	}

	@OnClick({R.id.back_btn, R.id.btn_setting_account, R.id.btn_setting_system, R.id.btn_setting_device, R.id.btn_setting_msg_remind, R.id.btn_setting_sync, R.id.btn_setting_version})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.btn_setting_account://修改账号
//				startActivity(new Intent(SettingActivity.this, LoginActivity.class));
				startActivity(new Intent(SettingActivity.this, GatewayStateActivity.class));
				break;
			case R.id.btn_setting_sync://同步数据
				showLoading("同步数据中...");
				UpdateDataService.updateData(LocalConstant.AREA_ALL);
				// 信息下发到7500串口
				try {
					mSerialPortUtil.sendMessageByArea(LocalConstant.AREA_ALL);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case R.id.btn_setting_system://系统设置
				//TODO 跳转系统设置选择框
				if (systemSettingFragment == null) {
					systemSettingFragment = new SystemSettingFragment();
				}
				systemSettingFragment.show(getSupportFragmentManager(), "systemSettingFragment");
				break;
			case R.id.btn_setting_msg_remind://消息提醒
				if (messageListFragment==null) {
					messageListFragment = new MessageListFragment();
				}
				messageListFragment.show(getSupportFragmentManager(),"messageListFragment");
				break;
			case R.id.btn_setting_version://当前版本
				startActivity(new Intent(getApplicationContext(),VersionInfo.class));
				break;
			case R.id.btn_setting_device://我的设备（设备巡检）
				if (deviceInspectionFragment == null) {
					deviceInspectionFragment = new DeviceInspectionFragment();
				}
				deviceInspectionFragment.show(getSupportFragmentManager(), "deviceInspectionFragment");
				break;
		}
	}

	@Subscribe
	public void recivieSetupLoadEvent(SetupActivityLoadEvent setupActivityLoadEvent) {
		hideLoading();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void syncData(UpdateFinishEvent event) {
		hideLoading();
		ToastUtil.showShort(BaseApp.getContext(), "同步数据成功");
	}

	/**
	 * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
	 */
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/**
	 * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
	 */
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));
	}

	/**
	 * 按名字清除本应用数据库 * * @param context * @param dbName
	 */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/**
	 * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
	 */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/**
	 * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
	 */
	public static void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}
