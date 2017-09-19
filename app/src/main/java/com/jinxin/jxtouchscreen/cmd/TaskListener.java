package com.jinxin.jxtouchscreen.cmd;

/**
 * Created by XTER on 2017/01/19.
 * 任务监听
 */
public abstract class TaskListener <T extends Task> {
	public abstract void onFail(T task, Object[] arg);

	public abstract void onSuccess(T task, Object[] arg);

	public void onAllSuccess(T task, Object[] arg) {

	}

	public void onAnyFail(T task, Object[] arg) {

	}
}
