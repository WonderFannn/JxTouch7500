package com.jinxin.jxtouchscreen.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jinxin.jxtouchscreen.activity.LoginActivity;

public class BootReceiver extends BroadcastReceiver {
	public BootReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent i = new Intent(context, LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
