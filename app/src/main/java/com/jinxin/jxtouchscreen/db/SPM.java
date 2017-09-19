package com.jinxin.jxtouchscreen.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.jinxin.jxtouchscreen.app.BaseApp;


/**
 * 配置存储管理--SharedPreferenceManager
 */
public class SPM {
	/**
	 * 配置表
	 */
	public static final String CONFIGURATION = "configuration";
	/**
	 * 存放普通常量
	 */
	public static final String ORDINARY_CONSTANTS = "constant";
	/**
	 * 存放常量
	 */
	public static final String CONSTANT = "constant";
	/**
	 * 存放位置信息
	 */
	public static final String LOCATION = "location";

	public static void saveBoolean(String name, String key, boolean value) {
		saveBoolean(BaseApp.getContext(), name, key, value);
	}

	public static void saveInt(String name, String key, int value) {
		saveInt(BaseApp.getContext(), name, key, value);
	}

	public static void saveStr(String name, String key, String value) {
		saveStr(BaseApp.getContext(), name, key, value);
	}

	public static boolean getBoolean(String name, String key, boolean defaultValue) {
		return getBoolean(BaseApp.getContext(), name, key, defaultValue);
	}

	public static int getInt(String name, String key, int defaultValue) {
		return getInt(BaseApp.getContext(), name, key, defaultValue);
	}

	public static String getStr(String name, String key, String defaultValue) {
		return getStr(BaseApp.getContext(), name, key, defaultValue);
	}

	private static void saveBoolean(Context context, String name, String key, boolean value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(key, value);
			editor.apply();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void saveInt(Context context, String name, String key, int value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt(key, value);
			editor.apply();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void saveStr(Context context, String name, String key, String value) {
		try {
			SharedPreferences sp = context.getSharedPreferences(name, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(key, value);
			editor.apply();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static boolean getBoolean(Context context, String name, String key, boolean defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		return sp != null && sp.getBoolean(key, defaultValue);
	}

	private static int getInt(Context context, String name, String key, int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		if (sp != null) {
			return sp.getInt(key, defaultValue);
		}
		return -1;
	}

	private static String getStr(Context context, String name, String key, String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(name, 0);
		if (sp != null) {
			return sp.getString(key, defaultValue);
		}
		return "";
	}

	/**
	 * 清除本地数据
	 *
	 * @param name 配置文件名
	 */
	public static void removeSP(String name) {
		SharedPreferences sp = BaseApp.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
		sp.edit().clear().apply();
	}

}
