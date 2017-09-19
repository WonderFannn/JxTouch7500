package com.jinxin.jxtouchscreen.cmd;

import android.content.Context;

import java.util.List;
import java.util.Stack;

/**
 * 离线命令处理的抽象类
 *
 * @author yang
 * @company 金鑫智慧
 */
public abstract class CmdSender<T> {
	protected Context context;
	protected Stack<T> cmdStack;
	protected TaskListener<Task> listener;

	/**
	 * 发送一条命令
	 *
	 * @param context 上下文
	 * @param cmd     命令
	 */
	public CmdSender(Context context, T cmd) {
		this.context = context;
		this.cmdStack = new Stack<>();
		this.cmdStack.add(cmd);
	}

	/**
	 * 默认的命令发送顺序
	 *
	 * @param context 上下文
	 * @param cmdList 命令列表
	 */
	public CmdSender(Context context, List<T> cmdList) {
		this(context, cmdList, false);
	}

	/**
	 * 指定命令放松顺序(在某些模块的命令发送中，如功放的命令发送，对命令的顺序有要求)
	 *
	 * @param context 上下文
	 * @param cmdList 命令列表
	 * @param fromTop 是否按顺序
	 */
	public CmdSender(Context context, List<T> cmdList, boolean fromTop) {
		this.context = context;
		this.cmdStack = new Stack<>();
		if (cmdList != null && cmdList.size() > 0) {
			if (fromTop) {
				for (int i = cmdList.size() - 1; i >= 0; i--) {
					cmdStack.add(cmdList.get(i));
				}
			} else {
				for (int i = 0; i < cmdList.size(); i++) {
					cmdStack.add(cmdList.get(i));
				}
			}
		}
	}

	public CmdSender<T> addListener(TaskListener<Task> listener) {
		this.listener = listener;
		return this;
	}

	abstract void send();
}
