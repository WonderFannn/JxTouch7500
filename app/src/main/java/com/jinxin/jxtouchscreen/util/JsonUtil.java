package com.jinxin.jxtouchscreen.util;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by XTER on 2016/9/20.
 * 解析辅助
 */
public class JsonUtil {
	public static String getString(JSONObject jo, String key) {
		if (jo.containsKey(key)) {
			return jo.getString(key);
		}
		return null;
	}

	public static int getInt(JSONObject jo, String key) {
		if (jo.containsKey(key)) {
			return jo.getIntValue(key);
		}
		return 0;
	}

	public static float getFloat(JSONObject jo, String key) {
		if (jo.containsKey(key)) {
			return jo.getFloatValue(key);
		}
		return 0;
	}

	public static double getDouble(JSONObject jo, String key) {
		if (jo.containsKey(key)) {
			return jo.getDoubleValue(key);
		}
		return 0.0;
	}

	public static long getLong(JSONObject jo, String key) {
		if (jo.containsKey(key)) {
			return jo.getLongValue(key);
		}
		return 0;
	}

	public static byte getByte(JSONObject jo, String key) {
		if (jo.containsKey(key)) {
			return jo.getByteValue(key);
		}
		return 0;
	}

	public static boolean getBoolean(JSONObject jo, String key) {
		if (jo.containsKey(key)) {
			return jo.getBooleanValue(key);
		}
		return false;
	}

}
