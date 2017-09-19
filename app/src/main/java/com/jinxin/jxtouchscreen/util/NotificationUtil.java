package com.jinxin.jxtouchscreen.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;

/**
 * Created by XTER on 2017/3/17.
 * 通知栏工具类
 */
public class NotificationUtil {
	private static NotificationUtil instance;
	private static NotificationManager mNotificationManager;
	private static Notification mNotification;
	private static NotificationCompat.Builder mBuilder;
	private static Handler mHandler = new Handler(Looper.getMainLooper());
	private static RemoteViews mContentView;

	private NotificationUtil() {
		mNotificationManager = (NotificationManager) BaseApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(BaseApp.getContext());
	}

	public static synchronized NotificationUtil getInstance() {
		if (instance == null) {
			instance = new NotificationUtil();
		}
		return instance;
	}

	public void updateDownloadView(final int percent) {
		if (mContentView == null)
			mContentView = new RemoteViews(BaseApp.getContext().getPackageName(), R.layout.item_notification_download);
		mBuilder.setAutoCancel(true);
		mBuilder.setContent(mContentView);
		mNotification = mBuilder.build();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mContentView.setTextViewText(R.id.tv_down_notification_title, "正在下载");
				mContentView.setTextViewText(R.id.tv_down_notification_percent, percent + "%");
				mContentView.setProgressBar(R.id.pb_down_notification, 100, percent, false);
				mNotificationManager.notify(1, mNotification);
			}
		});
	}
}
