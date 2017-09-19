package com.jinxin.jxtouchscreen.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.broad.BroadPushServerService;
import com.jinxin.jxtouchscreen.jpush.MessagePushThread;
import com.jinxin.jxtouchscreen.skin.SkinManager;
import com.jinxin.jxtouchscreen.util.L;

import java.util.List;


/**
 * Created by XTER on 2016/9/19.
 * 用静态内部类建单例，保证线程安全
 */
public class BaseApp extends Application {

	/**
	 * 是否要往大网关推送(尚未获取网关IP)
	 */
	public static  boolean IpFlag=true;

	/**
	 * 是否使用大网关
	 */
	public static boolean IpGateway = true;

	/**
	 * 是否可点击
	 */
	//public static boolean isClickable = true;
	public static boolean isClickable = false;

	private static BaseApp instance;

	public static BaseApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		L.DEBUG = true;
		// 设置你申请的应用讯飞appid
		StringBuffer param = new StringBuffer();
		param.append("appid="+getString(R.string.app_id));
		param.append(",");
		param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
		SpeechUtility.createUtility(BaseApp.this, param.toString());
		Setting.setShowLog(false);//是否显示讯飞LOG

		//大网关广播推送，接收端服务
		Intent PushService = new Intent(this, BroadPushServerService.class);
		startService(PushService);

		//不断从MessageQueue2取消息并推送给大网关
		Thread thread = new Thread(new MessagePushThread());
		thread.start();

//		startService(new Intent(this, UpdateDataService.class));
	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}

	/**
	 * 获取数据库的friendcontext
	 */
	public static Context getFriendContext(){
		Context friendContext = null;
		try {
			friendContext = getContext().createPackageContext("com.jinxin.gateway", Context.CONTEXT_IGNORE_SECURITY);
		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return friendContext;
	}

	/**
	 * 判断APP是否运行在后台
	 * @param context
	 * @return
     */
	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
				Log.i(context.getPackageName(), "此appimportace ="
						+ appProcess.importance
						+ ",context.getClass().getName()="
						+ context.getClass().getName());
				if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.i(context.getPackageName(), "处于后台"
							+ appProcess.processName);
					return true;
				} else {
					Log.i(context.getPackageName(), "处于前台"
							+ appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前在前台的activity
	 */
	public static String getRunningActivityName(){
		ActivityManager activityManager =
				(ActivityManager)BaseApp.instance.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		return runningActivity;
	}

	public static SkinManager getSkinManager() {
		return SkinManager.instance(getContext());
	}
}
