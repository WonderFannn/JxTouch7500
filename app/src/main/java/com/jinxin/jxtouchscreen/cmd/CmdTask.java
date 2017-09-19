package com.jinxin.jxtouchscreen.cmd;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.net.util.URLParser;
import com.jinxin.jxtouchscreen.serialport.SerialPortUtil;
import com.jinxin.jxtouchscreen.util.L;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

/**
 * Created by XTER on 2017/01/19.
 * 命令发送任务
 */
public class CmdTask extends Task {

	/**
	 * 命令连接器
	 */
	private IoConnector cmdConnector;

	/**
	 * 命令请求信息
	 */
	private CmdRequest cmdRequest;

	/**
	 * 命令请求类型 0--操作，1--查询
	 */
	private int operateType;

	/**
	 * 命令解析类型
	 * CMD_485--普通485
	 * CMD_ZG--zigbee偶数行解析
	 * CMD_ZG_INSPECT--zg巡检
	 * CMD_ZG_LOCK--ZG锁解析
	 */
	private CmdType cmdType;

	/**
	 * 命令归处
	 */
	private String host;

	/**
	 * 命令
	 */
	private byte[] cmd;

	private SerialPortUtil mSerialPortUtil;

	public CmdTask(int operateType, CmdType cmdType, String host, byte[] cmd) {
		this.operateType = operateType;
		this.cmdType = cmdType;
		this.host = host;
		this.cmd = cmd;
		this.mSerialPortUtil = SerialPortUtil.getInstance();
		this.init();
	}

	protected void init() {
		switch (cmdType) {
			//普通485设备
			case CMD_485:
				L.d("485 Cmd To " + host);
				CmdResponse cmdResponse0 = new CmdResponseFor485(this, cmd, operateType);
				cmdRequest = new CmdRequest(host, cmdResponse0);
				break;
			//普通Zigbee设备
			case CMD_ZG:
				L.d("Zigbee Cmd To " + host);
				CmdResponse cmdResponse1 = new CmdResponseForZigbee(this, cmd, CmdType.CMD_ZG, LocalConstant.CMD_ZG_DEVICE);
				cmdRequest = new CmdRequest(host, cmdResponse1);
				break;
			//zg巡检
			case CMD_ZG_INSPECT:
				L.d("Zigbee Check Cmd To " + host);
				CmdResponse cmdResponse2 = new CmdResponseForZigbee(this, cmd, CmdType.CMD_ZG_INSPECT, LocalConstant.CMD_ZG_DEVICE);
				cmdRequest = new CmdRequest(host, cmdResponse2);
				break;
			//zg锁
			case CMD_ZG_LOCK:
				L.d("Zigbee Lock Cmd To " + host);
				CmdResponse cmdResponse3 = new CmdResponseForZigbee(this, cmd, CmdType.CMD_ZG, LocalConstant.CMD_ZG_LOCK);
				cmdRequest = new CmdRequest(host, cmdResponse3);
				break;
		}
	}

	@Override
	public void excute() {
		// 信息下发到7500串口
		switch (cmdType) {
			//普通485设备
			case CMD_485:
				try {
					mSerialPortUtil.sendBuffer(1, cmd);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			//普通Zigbee设备
			case CMD_ZG:
				try {
					mSerialPortUtil.sendBuffer(2, cmd);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
		}
		if (cmdRequest == null) {
			L.e("命令请求为空");
			return;
		}

		if (cmdConnector == null)
			cmdConnector = new NioSocketConnector();

		// 设置链接超时时间
//		cmdConnector.setConnectTimeoutMillis(10 * 1000);
		cmdConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CmdCodecFactory()));
//		cmdConnector.getSessionConfig().setUseReadOperation(true);
		cmdConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,10);

		cmdConnector.setHandler(cmdRequest.getCmdResponse());

		IoSession session;
		try {
			URLParser urlParser = new URLParser(cmdRequest.getHost());
			String ip = urlParser.host;
			int port = urlParser.port;
			//创建连接
			ConnectFuture future = cmdConnector.connect(new InetSocketAddress(
					ip, port));
			//等待连接创建完成
			future.awaitUninterruptibly();
			//发送命令
			session = future.getSession();
			session.write(IoBuffer.wrap(cmd));

		} catch (Exception e) {
			L.e(e.getMessage());
		}
	}
}
