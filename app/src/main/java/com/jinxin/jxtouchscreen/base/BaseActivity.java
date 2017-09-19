package com.jinxin.jxtouchscreen.base;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.util.LoadingUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity {

	Dialog loadingDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		ButterKnife.bind(this);
		initData();
	}

	/**
	 * 设置视图
	 */
	protected abstract void initView();

	/**
	 * 初始化数据
	 */
	protected abstract void initData();

	/**
	 * 更新UI
	 * @param msg msg
	 */
	protected void uiHandleMessage(Message msg) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
	}

	protected static class BaseUIHandler extends Handler {
		WeakReference<BaseActivity> baseActivityWeakReference;

		public BaseUIHandler(BaseActivity baseActivity) {
			baseActivityWeakReference = new WeakReference<>(baseActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseActivity baseActivity = baseActivityWeakReference.get();
			baseActivity.uiHandleMessage(msg);
		}
	}

	protected void showLoading(String msg){
		loadingDialog = LoadingUtil.createLoadingDialog(this,msg);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	protected void hideLoading(){
		if(loadingDialog!=null)
			loadingDialog.dismiss();
		loadingDialog = null;
	}
}