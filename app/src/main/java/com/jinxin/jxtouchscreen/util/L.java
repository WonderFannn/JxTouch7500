package com.jinxin.jxtouchscreen.util;

import android.text.TextUtils;
import android.util.Log;

public class L {

	public static boolean DEBUG;

	public static void v(String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.v(getMethodPath(4, 4), msg);
	}

	public static void i(String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.i(getMethodPath(4, 4), msg);
	}

	public static void d(String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.d(getMethodPath(4, 4), msg);
	}

	public static void w(String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.w(getMethodPath(4, 4), msg);
	}

	public static void e(String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.e(getMethodPath(4, 4), msg);
	}

	public static void v(String tag,String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.v(TextUtils.isEmpty(tag)?getMethodPath(4,4):tag, msg);
	}

	public static void d(String tag,String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.d(TextUtils.isEmpty(tag)?getMethodPath(4,4):tag, msg);
	}

	public static void i(String tag,String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.i(TextUtils.isEmpty(tag)?getMethodPath(4,4):tag, msg);
	}

	public static void w(String tag,String msg) {
		if (DEBUG && !TextUtils.isEmpty(msg))
			Log.w(TextUtils.isEmpty(tag)?getMethodPath(4,4):tag, msg);
	}

	public static void e(String tag,String msg) {
		if (DEBUG && !TextUtils.isEmpty(tag))
			Log.e(TextUtils.isEmpty(tag)?getMethodPath(4,4):tag, msg);
	}

	/**
	 * 得到调用此方法的类名与方法名，默认下标为3
	 *
	 * @return string
	 */
	public static String getMethodPath() {
		return Thread.currentThread().getStackTrace()[3].getClassName() + "."
				+ Thread.currentThread().getStackTrace()[3].getMethodName() + "-->";
	}

	/**
	 * 得到调用此方法的类名与方法名
	 *
	 * @param classPrior  类级
	 * @param methodPrior 方法级
	 * @return string
	 */
	public static String getMethodPath(int classPrior, int methodPrior) {
		int length = Thread.currentThread().getStackTrace().length;
		if (classPrior > length || methodPrior > length) {
			return null;
		} else
			return Thread.currentThread().getStackTrace()[classPrior].getClassName() + "."
					+ Thread.currentThread().getStackTrace()[methodPrior].getMethodName() + "-->";
	}

	/**
	 * 测试方法，将线程中的序列全部输出
	 */
	public static void logThreadSequence() {
		int length = Thread.currentThread().getStackTrace().length;
		for (int i = 0; i < length; i++) {
			Log.i(Thread.currentThread().getStackTrace()[i].getClassName(),
					Thread.currentThread().getStackTrace()[i].getMethodName());
		}
	}
}
