package com.jinxin.jxtouchscreen.cmd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XTER on 2017/01/19.
 * 任务
 */
public abstract class Task implements Runnable {

	public static final int STATE_SUCCESS = 1;
	public static final int STATE_FAIL = 2;

	/**
	 * 当前任务状态
	 */
	private int mState;

	/**
	 * 任务成功
	 */
	private Object[] receiverSuccessObject;

	/**
	 * 任务失败
	 */
	private Object[] receiverFailObject;

	/**
	 * 任务监听器
	 */
	private List<TaskListener> listeners = new ArrayList<>();

	@Override
	public void run() {
		excute();
	}

	public void start() {
		new Thread(this).start();
	}

	public void onSuccess(Object... obj) {
		this.receiverSuccessObject = obj;
	}

	public void onFailed(Object... obj) {
		this.receiverFailObject = obj;
	}

	public void setState(int state){
		this.mState = state;
	}

	protected void onFinish() {
		if (mState == STATE_SUCCESS) {
			for (TaskListener tl : listeners) {
				tl.onSuccess(this, receiverSuccessObject);
			}
		}
		if (mState == STATE_FAIL) {
			for (TaskListener tl : listeners) {
				tl.onFail(this, receiverFailObject);
			}
		}
	}

	public void addListener(TaskListener listener) {
		listeners.add(listener);
	}

	abstract void excute();
}
