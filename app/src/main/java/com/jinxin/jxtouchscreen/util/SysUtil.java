package com.jinxin.jxtouchscreen.util;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jinxin.jxtouchscreen.app.BaseApp;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by XTER on 2016/9/20.
 * 系统相关
 */
public class SysUtil {

	/**
	 * 得到当前时间
	 *
	 * @return time
	 */
	public static String getNow() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return sdf.format(d);
	}

	/**
	 * 得到当前时间
	 *
	 * @return time
	 */
	public static String getNow2() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		return sdf.format(d);
	}

	/**
	 * 得到时间
	 *
	 * @param time 时间
	 * @return date
	 */
	public static Date getDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到固定格式时间
	 *
	 * @param date 时间
	 * @return time
	 */
	public static String getTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return sdf.format(date);
	}

	/**
	 * 得到固定格式时间
	 *
	 * @param time 时间
	 * @return time
	 */
	public static String getTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		try {
			return sdf.format(sdf1.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到转换日期
	 *
	 * @param time 数
	 * @return time
	 */
	public static String getDate(long time, String regex) {
		SimpleDateFormat sdf = new SimpleDateFormat(regex, Locale.CHINA);
		return sdf.format(time);
	}

	/**
	 * 得到转换时间
	 *
	 * @param time 数
	 * @return time
	 */
	public static String getTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return sdf.format(time);
	}

	/**
	 * 隐藏系统UI
	 *
	 * @param view 当前顶层视图--一般指decorview
	 */
	@SuppressLint("NewApi")
	public static void hideSystemUI(View view) {
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	/**
	 * 显示系统UI
	 *
	 * @param view 当前顶层视图--一般指decorview
	 */
	@SuppressLint("NewApi")
	public static void showSystemUI(View view) {
		view.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	/* 获得状态栏高度 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/* 获取操作拦高度 */
	public static int getActionBarHeight(Context context) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	/* 得到系统栏高度 */
	public static int getSystemBarHeight(Context context) {
		return getActionBarHeight(context) + getStatusBarHeight(context);
	}

	/* 得到系统栏（包括状态栏和操作栏）参数 */
	public static LinearLayout.LayoutParams getSystemBarParam(Context context) {
		int occupyHeight = getActionBarHeight(context) + getStatusBarHeight(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, occupyHeight);
		return layoutParams;
	}

	public static int getScreenWidth() {
		DisplayMetrics dm = BaseApp.getContext().getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeight() {
		DisplayMetrics dm = BaseApp.getContext().getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * 执行shell命令
	 *
	 * @param cmd 命令
	 */
	public static void execShellCmd(String cmd) {

		try {
			// 申请获取root权限，这一步很重要，不然会没有作用
			Process process = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(
					outputStream);
			dataOutputStream.writeBytes(cmd);
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * 模拟按键
	 *
	 * @param keyCode 键值
	 */
	public static void execShellCmd(final int keyCode) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				Instrumentation inst = new Instrumentation();
				inst.sendKeyDownUpSync(keyCode);
//			}
//		}).start();
	}
	/**
	 * 将String转换为utf-8
	 *
	 */
	public static String getUTF8XMLString(String xml) {
		// A StringBuffer Object
		StringBuffer sb = new StringBuffer();
		sb.append(xml);
		String xmString = "";
		String xmlUTF8="";
		try {
			xmString = new String(sb.toString().getBytes("UTF-8"));
			xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return to String Formed
		return xmlUTF8;
	}
}
