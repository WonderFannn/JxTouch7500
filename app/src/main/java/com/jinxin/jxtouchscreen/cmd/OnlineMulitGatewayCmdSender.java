package com.jinxin.jxtouchscreen.cmd;

import android.content.Context;


import com.jinxin.jxtouchscreen.util.L;

import java.util.List;

/**
 * 多网关在线发送命令
 *
 * @author TangLong
 */
public class OnlineMulitGatewayCmdSender extends CmdSender<Command> {

	private boolean isAllCmdSuccessed = true;

	public OnlineMulitGatewayCmdSender(Context context, List<Command> cmdList) {
		super(context, cmdList);
	}

	@Override
	public void send() {
		L.w("command size:" + cmdStack.size());
		if (cmdStack == null)
			return;
		if (!cmdStack.isEmpty()) {
			Command command = cmdStack.pop();
			if (command == null)
				return;

			// debug
			L.w(command.toString());
			List<byte[]> cmds = command.getCmdList();
			for (byte[] b : cmds) {
				L.d("OnlineMulitGatewayCmdSender--> " + new String(b));
			}

			// send cmd
			OnlineCmdSenderLong cmdSender = new OnlineCmdSenderLong(context,
					command.getGatewayRemoteIp(), command.isZigbee() ? CmdType.CMD_ZG : CmdType.CMD_485, command.getCmdList(), true, 0);
			cmdSender.addListener(new TaskListener<Task>() {

				@Override
				public void onSuccess(Task task, Object[] arg) {
					if (cmdStack.isEmpty() && isAllCmdSuccessed)
						listener.onAllSuccess(task, arg);
					send();
				}

				@Override
				public void onFail(Task task, Object[] arg) {
					if (cmdStack.isEmpty() && !isAllCmdSuccessed)
						listener.onAnyFail(task, arg);
					send();
				}

			});
			cmdSender.send();
		}
	}

}
