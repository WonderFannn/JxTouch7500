package com.jinxin.jxtouchscreen.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.model.MessageTimer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by XTER on 2016/9/20.
 * 应用相关
 */
public class AppUtil {

	private static final String HEX_STRING_FLAG = "0x";
	private static final String OCT_STRING_0 = "0";

	/**
	 * 退出
	 */
	public static void exit() {
		//获取PID
		android.os.Process.killProcess(android.os.Process.myPid());
		//常规java、c#的标准退出法，返回值为0代表正常退出
		System.exit(0);
	}

	public static void restartApp(){
		Intent i = BaseApp.getContext().getPackageManager()
				.getLaunchIntentForPackage(BaseApp.getContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		BaseApp.getContext().startActivity(i);
	}

	/**
	 * 清除缓存
	 */
	public static void clearCache(){

	}

	/**
	 * 当前登录账号
	 *
	 * @return account
	 */
	public static String getCurrentAccount() {
		String account = SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, "");
		String subAccount = SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_SUB_ACCOUNT, "");
		if (TextUtils.isEmpty(subAccount)) {
			if (TextUtils.isEmpty(account)) {
				return "";
			} else {
				return account;
			}
		} else {
			return subAccount;
		}
	}

	/**
	 * 获取默认扬声器设置
	 */
	public static ArrayList<String> getDefaultSpeakerNames() {
		String[] speakers = BaseApp.getContext().getResources().getStringArray(R.array.speaker_items);
		ArrayList<String> list = new ArrayList<String>();
		Collections.addAll(list, speakers);
		return list;
	}

	/**
	 * 获取扬声器名字
	 */
	public static ArrayList<String> getSpeakerNames() {
		ArrayList<String> speakerList = new ArrayList<String>();
		String speakerNamesStr = DBM.getSpeakerNamesFromDb(ProductConstants.FUN_TYPE_POWER_AMPLIFIER);
		if (TextUtils.isEmpty(speakerNamesStr)) {
			try {
				JSONObject jsonObj = new JSONObject(speakerNamesStr);
				JSONArray jsonArr = jsonObj.getJSONArray("param");

				for (int i = 0; i < jsonArr.length(); i++) {
					speakerList.add((String) jsonArr.get(i));
				}

				return speakerList;
			} catch (JSONException e) {
				return getDefaultSpeakerNames();
			}
		} else {
			return getDefaultSpeakerNames();
		}
	}

	/**
	 * 取皮肤包asset下images包图片（未取到为null）
	 *
	 * @return eg:
	 */
	public static Bitmap getAssetsBitmap(Context context, String path) {
		if (context == null) return null;

		InputStream is = null;
		Bitmap bitmap = null;
		try {
			is = context.getAssets().open(path);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally{
			FileUtil.closeInputStream(is);
		}
		return bitmap;
	}

	/**
	 * 报警通知栏
	 *
	 * @param context
	 * @param content
	 * @param isOutside 户外模式  or 居家模式
	 */
	@SuppressLint({"NewApi", "SimpleDateFormat"})
	public static void showNotification(Context context, String content, boolean isOutside) {
		String _account = AppUtil.getCurrentAccount();
		boolean isOpenNotice = false; //是否开启了分时断提醒
		boolean noShake = SPM.getBoolean(_account, NetConstant.KEY_WARN_SHAKE, false);
		isOpenNotice = SPM.getBoolean(_account, NetConstant.KEY_NOTICE_ON_OFF, true);
		boolean isNotice = true;//是否发送通知

		if (isOpenNotice) {
			List<MessageTimer> list = DBM.getCurrentOrm().query(MessageTimer.class);
			if (list != null && list.size() > 0) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");//只有时分
				String currentTime = sdf.format(date);
				for (MessageTimer messageTimer : list) {
					String[] arr = messageTimer.getTimeRange().split("-");
					int l = largerTime(arr[0], currentTime);
					int r = largerTime(arr[1], currentTime);
					if ((l + r) == 0) {
						isNotice = true;
						break;
					} else {
						isNotice = false;
					}
				}
			} else {
				isNotice = true;
			}
		} else {
			L.d(null, "消息提醒已关闭");
			return;
		}

		if (!isNotice) return;

		// 创建一个NotificationManager的引用
		NotificationManager mNotifyMgr =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int mNotificationId = 001;
		// 创建一个NotificationCompat.Builder
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle("智慧家居应用提示")
						.setContentText(content)
						.setWhen(System.currentTimeMillis())
						.setAutoCancel(true);
		if (!isOutside) {//居家模式
			if (noShake) {//居家免打扰
				mBuilder.setVibrate(null);
				mBuilder.setSound(null);
			} else {
				mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
				mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring_tips));
			}
		} else {//户外模式
			mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ring_warnning));
		}
		// 把Builder传递给NotificationManager
		if (mNotifyMgr != null) {
			mNotifyMgr.notify(mNotificationId, mBuilder.build());
		}
	}

	public static void showNotification(String content){
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(BaseApp.getContext())
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle("智慧家居应用提示")
						.setContentText(content)
						.setWhen(System.currentTimeMillis())
						.setAutoCancel(true);
		// 创建一个NotificationManager的引用
		NotificationManager mNotifyMgr =
				(NotificationManager) BaseApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(2, mBuilder.build());
	}

	private static int largerTime(String t1, String t2) {
		Date date1, date2;
		DateFormat formart = new SimpleDateFormat("hh:mm");
		try {
			date1 = formart.parse(t1);
			date2 = formart.parse(t2);
			if (date1.compareTo(date2) < 0) {
				return -1;
			} else if (date1.compareTo(date2) == 0) {
				return 0;
			} else {
				return 1;

			}
		} catch (ParseException e) {
			System.out.println("date init fail!");
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * 返回当前程序版本名
	 */
	public static int getAppVersionCode(Context context) {
		int versioncode = 0;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versioncode = pi.versionCode;
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versioncode;
	}

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	/**
	 * APK安装
	 *
	 * @param path
	 *            apk路径
	 */
	public static void installAPK(Context context, String path) {
		if (path == null || path.length() < 0)
			return;
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 通过系统广播安装apk
	 */
	public static void installAPKBySystem() {
		Intent intent = new Intent();
		intent.setAction("com.jinxin.action.UPDATE_SYSTEM_APP");
		intent.putExtra("name", "com.jinxin.action.UPDATE_SYSTEM_APP");
		BaseApp.getContext().sendBroadcast(intent);
	}

	/**
	 * 备份apk文件
	 * @param path 地址
	 */
	public static void backupAppFile(String path) {
		try {
			String filePath = path.substring(path.lastIndexOf("jxtouch"), path.length());
			FileUtil.copyFile(new File(path), new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/backup_apk/" + filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 版本号比较
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2) {
		if (version1.equals(version2)) {
			return 0;
		}
		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");
		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;
		while (index < minLen
				&& (diff = Integer.parseInt(version1Array[index])
				- Integer.parseInt(version2Array[index])) == 0) {
			index++;
		}
		if (diff == 0) {
			for (int i = index; i < version1Array.length; i++) {
				if (Integer.parseInt(version1Array[i]) > 0) {
					return 1;
				}
			}
			for (int i = index; i < version2Array.length; i++) {
				if (Integer.parseInt(version2Array[i]) > 0) {
					return -1;
				}
			}
			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}
}
