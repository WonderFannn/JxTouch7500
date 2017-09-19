package com.jinxin.jxtouchscreen.jpush;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.util.L;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * 消息推送线程,暂时不考虑有返回值情况
 *
 * @version V1.0
 * @author: Guan.yuxuan.CD
 * @date 2014-3-10 下午2:08:26
 */
public class MessagePushThread implements Runnable {

	private static final String BROAD_ADDRESS = "255.255.255.255";
	private static final int BROAD_PORT = 10000;
	private static final int TIMEOUT = 3000; // 设置超时为3秒

	public MessagePushThread() {
	}

	@Override
	public void run() {

		while (true) {
			try {
				BroadPushMessage message = MessageQueue2.getInstance().poll();
				if (message != null) {
					sendBroad(message);
				}
				Thread.sleep(3000);
			} catch (Exception e) {
				L.e("推送消息失败");
			}
		}

	}

	private void sendBroad(BroadPushMessage message) {
		if (TextUtils.isEmpty(message.getContent()))
			return;
		message.setContent(genJSONString(message) + "\r\n");
		DatagramSocket socket = null;
		try {
			InetAddress serverAddress = InetAddress.getByName(BROAD_ADDRESS); // 服务器地址
			// 发送的信息
			byte[] bytesToSend = message.getContent().getBytes("UTF-8");
			socket = new DatagramSocket();
			socket.setSoTimeout(TIMEOUT); // 设置阻塞时间
			DatagramPacket sendPacket = new DatagramPacket(bytesToSend, // 相当于将发送的信息打包
					bytesToSend.length, serverAddress, BROAD_PORT);

			DatagramPacket receivePacket = // 相当于空的接收包
					new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);
			boolean receivedResponse = false;
			socket.send(sendPacket); // 发送信息
		} catch (Exception e) { // 当receive不到信息或者receive时间超过3秒时，就向服务器重发请求
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}

	}

	private String genJSONString(BroadPushMessage message) {
		JSONObject jObj = new JSONObject();
		jObj.put("extra", message.getContent());
		jObj.put("uId", message.getTarget());
		return jObj.toJSONString();
	}

}
