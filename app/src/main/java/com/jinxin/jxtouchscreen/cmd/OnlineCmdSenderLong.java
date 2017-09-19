package com.jinxin.jxtouchscreen.cmd;

import android.content.Context;

import com.jinxin.jxtouchscreen.util.L;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线发送命令
 *
 * @author TangLong
 * @company 金鑫智慧
 */
public class OnlineCmdSenderLong extends CmdSender<byte[]> {

	private int type;
	private CmdType cmdType;
	private String host;
	private List<byte[]> cmdList = new ArrayList<>();

	/**
	 * 发送一条命令
	 *
	 * @param context
	 * @param cmd     命令集合
	 */
	public OnlineCmdSenderLong(Context context, byte[] cmd) {
		super(context, cmd);
		this.cmdList.add(cmd);
	}

	/**
	 * 默认的命令发送顺序
	 *
	 * @param context
	 * @param cmdList 命令集合
	 */
	public OnlineCmdSenderLong(Context context, List<byte[]> cmdList) {
		this(context, cmdList, false);
	}


	/**
	 * 指定命令放松顺序(在某些模块的命令发送中，如功放的命令发送，对命令的顺序有要求)
	 *
	 * @param context
	 * @param cmdList 命令集合
	 * @param fromTop 是否按顺序发送命令
	 */
	public OnlineCmdSenderLong(Context context, List<byte[]> cmdList, boolean fromTop) {
		this(context, null, cmdList, fromTop);
	}

	/**
	 * 指定命令发送的目的服务器和发送顺序
	 *
	 * @param context
	 * @param host    地址
	 * @param cmdList 命令集合
	 * @param fromTop 是否按顺序发送命令
	 */
	public OnlineCmdSenderLong(Context context, String host, List<byte[]> cmdList, boolean fromTop) {
		this(context, host, CmdType.CMD_485, cmdList, fromTop, 0);
	}

	/**
	 * 指定命令发送的目的服务器和发送顺序以及命令类型
	 *
	 * @param context 上下文
	 * @param host    目的地址
	 * @param cmdType 命令类型0:有线 1:无线 2:无线巡检 3:无线锁
	 * @param cmdList 命令
	 * @param fromTop 是否按顺序
	 * @param type    操作类型0/查询类型1
	 */
	public OnlineCmdSenderLong(Context context, String host, CmdType cmdType, List<byte[]> cmdList, boolean fromTop, int type) {
		super(context, cmdList, fromTop);
		this.cmdType = cmdType;
		this.host = host;
		this.type = type;
		this.cmdList = cmdList;
	}

	@Override
	public void send() {
		L.d("************** 命令开始发送 **************");
		/*----------合并所有指令一次发送----------------- */
		if (cmdList != null && cmdList.size() > 0) {
			byte[] allCmd = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				for (int i = 0; i < cmdList.size(); i++) {
					if (cmdList.get(i) != null) {
						bos.write(cmdList.get(i));
					}
				}
				allCmd = bos.toByteArray();
				L.d("send: " + new String(allCmd, Charset.forName("UTF-8")));
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (allCmd != null && host != null) {
				CmdTask cmdTask = new CmdTask(type, cmdType, host, allCmd);
				cmdTask.addListener(listener);
				cmdTask.start();
			}
		}

	}
}