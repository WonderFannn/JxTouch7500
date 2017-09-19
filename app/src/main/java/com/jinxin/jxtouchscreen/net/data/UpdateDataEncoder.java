package com.jinxin.jxtouchscreen.net.data;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.util.L;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Created by XTER on 2017/2/14.
 * 数据通信编码
 */
public class UpdateDataEncoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
		JSONObject jo = (JSONObject) o;

		String content = jo.toJSONString();
		L.d("发送:" + content);
		int len = content.length();

		//判断报文类型
		int type = 0;
		if (jo.containsKey("area")) {
			switch (jo.getString("area")) {
				case "alarm":
				case "state":
					type = 9;
					break;
				case LocalConstant.UPDATE:
					type = 5;
					break;
				case LocalConstant.GATEWAY_INFO:
				case LocalConstant.GATEWAY_LOG:
					type = 4;
					break;
				default:
					type = 3;
					break;
			}
		} else if (jo.containsKey("heartbeat")) {
			type = 2;
		}

		IoBuffer ioBuffer = IoBuffer.allocate(len + 8);
		ioBuffer.putInt(type);
		ioBuffer.putInt(len);
		ioBuffer.put(content.getBytes("utf-8"));
		ioBuffer.flip();
		protocolEncoderOutput.write(ioBuffer);
		ioBuffer.free();
	}

	@Override
	public void dispose(IoSession ioSession) throws Exception {

	}
}
