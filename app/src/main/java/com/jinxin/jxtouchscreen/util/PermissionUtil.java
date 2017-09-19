package com.jinxin.jxtouchscreen.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.jinxin.jxtouchscreen.app.BaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XTER on 2017/03/15.
 * 权限请求
 */
public class PermissionUtil {

	public interface PermissionGrant {
		void onPermissionGranted(int requestCode);
	}

	public static final int CODE_RECORD_AUDIO = 0;
	public static final int CODE_GET_ACCOUNTS = 1;
	public static final int CODE_READ_PHONE_STATE = 2;
	public static final int CODE_CALL_PHONE = 3;
	public static final int CODE_CAMERA = 4;
	public static final int CODE_ACCESS_FINE_LOCATION = 5;
	public static final int CODE_ACCESS_COARSE_LOCATION = 6;
	public static final int CODE_WRITE_EXTERNAL_STORAGE = 7;
	public static final int CODE_SYSTEM_ALERT_WINDOW = 8;
	public static final int CODE_MULTI_PERMISSION = 100;

	private static final String[] requestPermissions = {
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.GET_ACCOUNTS,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.CALL_PHONE,
			Manifest.permission.CAMERA,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.SYSTEM_ALERT_WINDOW
	};

	public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
		if (activity == null) {
			return;
		}

		if (requestCode < 0 || requestCode >= requestPermissions.length) {
			L.w("非法权限请求码:" + requestCode);
			return;
		}

		final String requestPermission = requestPermissions[requestCode];

		//如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
		// 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
		// 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，

		int checkSelfPermission;
		try {
			checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
		} catch (RuntimeException e) {
			ToastUtil.showShort(BaseApp.getContext(), "请开启权限");
			L.e(e.getMessage());
			return;
		}

		if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
				shouldShowRationale(activity, requestCode, requestPermission);
			} else {
				ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
			}
		} else {
			permissionGrant.onPermissionGranted(requestCode);
		}
	}

	private static void shouldShowRationale(final Activity activity, final int requestCode, final String requestPermission) {
		new AlertDialog.Builder(activity)
				.setMessage("请求权限")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityCompat.requestPermissions(activity,
								new String[]{requestPermission},
								requestCode);
					}
				})
				.setNegativeButton("否", null)
				.create()
				.show();
	}

	public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
	                                            @NonNull int[] grantResults, PermissionGrant permissionGrant) {

		if (activity == null) {
			return;
		}

		if (requestCode == CODE_MULTI_PERMISSION) {
			requestMultiResult(activity, permissions, grantResults, permissionGrant);
			return;
		}

		if (requestCode < 0 || requestCode >= requestPermissions.length) {
			return;
		}

		if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			permissionGrant.onPermissionGranted(requestCode);
		} else {
			openSettingActivity(activity, "打开设置页面");
		}

	}

	private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {
		if (activity == null) {
			return;
		}
		Map<String, Integer> perms = new HashMap<>();

		ArrayList<String> notGranted = new ArrayList<>();
		for (int i = 0; i < permissions.length; i++) {
			perms.put(permissions[i], grantResults[i]);
			if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
				notGranted.add(permissions[i]);
			}
		}

		if (notGranted.size() == 0) {
			permissionGrant.onPermissionGranted(CODE_MULTI_PERMISSION);
		} else {
			openSettingActivity(activity, "those permission need granted!");
		}

	}

	private static void openSettingActivity(final Activity activity, String message) {
		new AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
						intent.setData(uri);
						activity.startActivity(intent);
					}
				})
				.setNegativeButton("否", null)
				.create()
				.show();
	}
}
